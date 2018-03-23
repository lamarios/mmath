package com.ftpix.mmath.redditbot;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.MmathFighter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Comment;
import net.dean.jraw.oauth.Credentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MmathBot {
    private final MySQLDao dao;
    private final OrientDBDao orientDBDao;
    private final String subreddit;
    private final Pattern pattern;
    private RedditBot bot;
    private final ExecutorService exec = Executors.newFixedThreadPool(4);


    private final static String COMMENT_REGEX = "^( )*!mmath( )+(([a-zA-Z. ])+)( )+vs( )+(([a-zA-Z. ])+)( )*$";
    private final static String NEW_LINE = "%0A";
    private final static String BOT_REPLY = "MmathBot \n\n " +
            "[**%s** vs. **%s**](https://mmathbro.science/%s/vs/%s) \n\n " +
            "* %s \n\n " +
            "* %s \n\n\n\n\n\n ";

    private Logger logger = LogManager.getLogger();

    public MmathBot(MySQLDao dao, OrientDBDao orientDBDao, String subreddit) {
        this.dao = dao;
        this.orientDBDao = orientDBDao;
        this.subreddit = subreddit;

        pattern = Pattern.compile(COMMENT_REGEX);

        startBot();
    }

    private void startBot() {
        String username = Optional.ofNullable(System.getProperty("reddit.username")).orElseThrow(InvalidParameterException::new);
        String password = Optional.ofNullable(System.getProperty("reddit.password")).orElseThrow(InvalidParameterException::new);
        String clientId = Optional.ofNullable(System.getProperty("reddit.client.id")).orElseThrow(InvalidParameterException::new);
        String secret = Optional.ofNullable(System.getProperty("reddit.secret")).orElseThrow(InvalidParameterException::new);

        Credentials oauthCreds = Credentials.script(username, password, clientId, secret);

// Create a unique User-Agent for our bot
        UserAgent userAgent = new UserAgent("bot", "science.mmathbro.bot", "1.0.0",username);

//        logger.info("Creating reddit bot using account: {}, clientid:{} secret:{} password:{}", username, clientId, secret, password);
        bot = new RedditBot.Builder(oauthCreds, userAgent)
                .followingSubReddit(subreddit)
                .withPullDelay(10_000)
                .filterComments(c -> c.getBody().trim().matches(COMMENT_REGEX))
                .onNewComment(this::processComment)
                .build();

        bot.startAsync();
    }

    private void processComment(Comment comment) {
        try {
            logger.info("Got comment: {}", comment.getBody());
            Matcher match = pattern.matcher(comment.getBody().trim());
            if (match.matches()) {
                String fighter1 = match.group(3);
                String fighter2 = match.group(7);
                logger.info("{} vs {}", fighter1, fighter2);

                Future<Optional<MmathFighter>> mmathFighter1Future = exec.submit(() -> getFirstFighterForName(fighter1));
                Future<Optional<MmathFighter>> mmathFighter2Future = exec.submit(() -> getFirstFighterForName(fighter2));

                Optional<MmathFighter> mmathFighter1 = mmathFighter1Future.get();
                Optional<MmathFighter> mmathFighter2 = mmathFighter2Future.get();

                if (mmathFighter1.isPresent() && mmathFighter2.isPresent()) {
                    MmathFighter f1 = mmathFighter1.get();
                    MmathFighter f2 = mmathFighter2.get();
                    Future<List<String>> f1Vsf2 = exec.submit(() -> orientDBDao.findShortestPath(f1, f2));
                    Future<List<String>> f2Vsf1 = exec.submit(() -> orientDBDao.findShortestPath(f2, f1));

                    List<String> f1Vsf2List = f1Vsf2.get();
                    List<String> f2Vsf1List = f2Vsf1.get();


                    String f1Vsf2Result = f1Vsf2List.stream()
                            .filter(s -> s != null)
                            .map(f -> dao.getFighterDAO().getById(f).getName())
                            .collect(Collectors.joining(" > "));
                    String f2Vsf1Result = f2Vsf1List.stream()
                            .filter(s -> s != null)
                            .map(f -> dao.getFighterDAO().getById(f).getName())
                            .collect(Collectors.joining(" > "));

                    logger.info("result 1 vs 2: {}", f1Vsf2Result);
                    logger.info("result 2 vs 1: {}", f2Vsf1Result);

                    if (f1Vsf2Result.length() == 0) {
                        f1Vsf2Result = f1.getName() + " can't beat " + f2.getName();
                    }
                    if (f2Vsf1Result.length() == 0) {
                        f2Vsf1Result = f2.getName() + " can't beat " + f1.getName();
                    }

                    String reply = String.format(BOT_REPLY, f1.getName(), f2.getName(), f1.getIdAsHash(), f2.getIdAsHash(), f1Vsf2Result, f2Vsf1Result).replace("\n", NEW_LINE);
                    logger.info("Replying {}", reply);
                    bot.getClient().comment(comment.getId()).reply(reply);

                } else {
                    logger.info("No fighters...");
                }


            } else {
                logger.info("Not matching");
            }
        } catch (Exception e) {
            logger.error("Couldn't proceed to calculation of comment: [{}]", comment.getBody(), e);
        }
    }


    private Optional<MmathFighter> getFirstFighterForName(String s) {
        return dao.getFighterDAO().searchByName(s).stream().findFirst();
    }
}
