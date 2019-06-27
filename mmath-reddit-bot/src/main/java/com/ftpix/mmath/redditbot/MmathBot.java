package com.ftpix.mmath.redditbot;

import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.HypeTrain;
import com.ftpix.mmath.model.MmathFighter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Comment;
import net.dean.jraw.oauth.Credentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
public class MmathBot {


    @Autowired
    private OrientDBDao orientDBDao;

    private final Pattern mmathPattern, hypetrainPatter;
    private RedditBot bot;
    private final ExecutorService exec = Executors.newFixedThreadPool(4);

    private final static String HYPETRAIN_PATTERN = "^(?:.*)(!onboard|!offboard)(?: +)([a-zA-Z. ]+)(?:.*)$";
    private final static String MMATH_PATTERN = "^(?:.*)!mmath(?: )+([a-zA-Z. ]+)(?: +)vs(?: +)([a-zA-Z. ]+)(?:.*)$";
    private final static String PATTERN = MMATH_PATTERN + "|" + HYPETRAIN_PATTERN;
    private final static String NEW_LINE = "%0A";
    private final static String MMATH_BOT_REPLY = "[**%s** vs. **%s**](https://mmathbro.science/%s/vs/%s) \n\n " +
            "* %s \n\n " +
            "* %s \n\n\n\n\n\n ";
    private final static String HYPE_TRAIN_ONBOARD_REPLY = "You jumped on board the [**%s**](https://www.mmahypetrain.com/fighter/%s) train, you're **%s** on board ! \n\n " +
            "[Manage your hype](https://www.mmahypetrain.com)";
    private final static String HYPE_TRAIN_OFFBOARD_REPLY = "You jumped off the [**%s**](https://www.mmahypetrain.com/fighter/%s) train, **%s** left on board. \n\n " +
            "[Manage your hype](https://www.mmahypetrain.com)";
    private final static String FIGHTER_NOT_FOUND_REPLY = "Couldn't find fighter \"%s\"";
    private Logger logger = LogManager.getLogger();


    @Value("${REDDIT_CLIENT_ID}")
    private String redditClientId;

    @Value("${REDDIT_SECRET}")
    private String redditSecret;


    @Value("${REDDIT_USERNAME}")
    private String redditUsername;


    @Value("${REDDIT_PASSWORD}")
    private String redditPassword;


    @Value("${REDDIT_SUB}")
    private String subreddit;


    @Value("${REDDIT_BOT_OWNER}")
    private String redditBotOwner;




    @Autowired
    private FighterDAO fighterDAO;



    @Autowired
    private HypeTrainDAO hypeTrainDAO;

    public MmathBot() {

        mmathPattern = Pattern.compile(MMATH_PATTERN);
        hypetrainPatter = Pattern.compile(HYPETRAIN_PATTERN);

    }


    @PostConstruct
    private void startBot() {
        Credentials oauthCreds = Credentials.script(redditUsername, redditPassword, redditClientId, redditSecret);

// Create a unique User-Agent for our bot
        UserAgent userAgent = new UserAgent("linux-docker", "science.mmathbro.bot", "1.0.0", redditBotOwner);

//        logger.info("Creating reddit bot using account: {}, clientid:{} redditSecret:{} redditPassword:{}", redditUsername, redditClientId, redditSecret, redditPassword);
        bot = new RedditBot.Builder(oauthCreds, userAgent)
                .followingSubReddit(subreddit)
                .withPullDelay(60_000)
                .filterComments(c -> c.getBody().trim().matches(PATTERN))
                .onNewComment(this::processComment)
                .build();

        bot.startAsync();
    }

    private void processComment(Comment comment) {
        if (comment.getBody().trim().matches(MMATH_PATTERN)) {
            processMMathComment(comment);
        } else if (comment.getBody().trim().matches(HYPETRAIN_PATTERN)) {
            processHypeTrainComment(comment);
        }
    }

    /**
     * Processes a Hypetrain comment
     *
     * @param comment
     */
    private void processHypeTrainComment(Comment comment) {
        try {

            Matcher match = hypetrainPatter.matcher(comment.getBody().trim());
            if (match.matches()) {
                String onOff = match.group(1);
                String fighter = match.group(2);
                String user = "/u/" + comment.getAuthor();
                Optional<MmathFighter> fighterOpt = getFirstFighterForName(fighter);

                String reply = "";
                if (fighterOpt.isPresent()) {
                    MmathFighter mmathFighter = fighterOpt.get();
                    HypeTrain hypeTrain = new HypeTrain(user, mmathFighter.getSherdogUrl());
                    switch (onOff) {
                        case "!onboard":
                            hypeTrainDAO.insert(hypeTrain);
                            reply = HYPE_TRAIN_ONBOARD_REPLY;
                            break;
                        case "!offboard":
                            hypeTrainDAO.deleteById(hypeTrain);
                            reply = HYPE_TRAIN_OFFBOARD_REPLY;
                            break;
                    }

                    Long count = Optional.ofNullable(hypeTrainDAO.countForFighter(mmathFighter.getSherdogUrl()))
                            .orElse(0L);

                    reply = String.format(reply, mmathFighter.getName(), mmathFighter.getIdAsHash(), count);

                } else {
                    reply = String.format(FIGHTER_NOT_FOUND_REPLY, fighter);
                }


                if (reply.length() > 0) {
                    logger.info("Replying {}", reply);
                    bot.getClient().comment(comment.getId()).reply(reply);
                }


            }
        } catch (Exception e) {
            logger.error("Couldn't proceed to calculation of comment: [{}]", comment.getBody(), e);
        }
    }

    /**
     * Processes an !mmath comment
     *
     * @param comment
     */
    private void processMMathComment(Comment comment) {
        try {
            logger.info("Got comment: {}", comment.getBody());
            Matcher match = mmathPattern.matcher(comment.getBody().trim());
            if (match.matches()) {
                String fighter1 = match.group(1);
                String fighter2 = match.group(2);
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
                            .map(f -> fighterDAO.getById(f).getName())
                            .collect(Collectors.joining(" > "));
                    String f2Vsf1Result = f2Vsf1List.stream()
                            .filter(s -> s != null)
                            .map(f -> fighterDAO.getById(f).getName())
                            .collect(Collectors.joining(" > "));

                    logger.info("result 1 vs 2: {}", f1Vsf2Result);
                    logger.info("result 2 vs 1: {}", f2Vsf1Result);

                    if (f1Vsf2Result.length() == 0) {
                        f1Vsf2Result = f1.getName() + " can't beat " + f2.getName();
                    }
                    if (f2Vsf1Result.length() == 0) {
                        f2Vsf1Result = f2.getName() + " can't beat " + f1.getName();
                    }

                    String reply = String.format(MMATH_BOT_REPLY, f1.getName(), f2.getName(), f1.getIdAsHash(), f2.getIdAsHash(), f1Vsf2Result, f2Vsf1Result).replace("\n", NEW_LINE);
                    logger.info("Replying {}", reply);
                    bot.getClient().comment(comment.getId()).reply(reply);

                } else {
                    String reply = "";
                    if (!mmathFighter1.isPresent()) {
                        reply = String.format(FIGHTER_NOT_FOUND_REPLY, fighter1);
                    } else if (!mmathFighter2.isPresent()) {
                        reply = String.format(FIGHTER_NOT_FOUND_REPLY, fighter2);
                    }

                    if (reply.length() > 0) {
                        logger.info("Replying {}", reply);
                        bot.getClient().comment(comment.getId()).reply(reply);
                    }
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
        return fighterDAO.searchByName(s).stream().findFirst();
    }
}
