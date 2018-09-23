package com.ftpix.hypetrain.web.controller;

import com.ftpix.hypetrain.web.GsonTransformer;
import com.ftpix.hypetrain.web.TrainGenerator;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.AggregatedHypeTrain;
import com.ftpix.mmath.model.HypeTrain;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sparknnotation.annotations.*;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;


@SparkController
public class HypeTrainController {

    private static final String SESSION_LOGIN_STRING = "loginString";
    private static final String REDDIT_USER_AGENT = "web:com.mmathypetrain:1.0 (by /u/lamarios)";
    private static final String SESSION_USERNAME = "username";

    private final String REDIRECT_URL = "http://localhost:15679/post-login";
    private final String redditSecret;
    private final String redditClientId;

    private Logger logger = LogManager.getLogger();

    private final MySQLDao dao;

    public HypeTrainController(MySQLDao dao) {
        this.dao = dao;

        redditClientId = Optional.ofNullable(System.getProperty("reddit.client.id")).orElseThrow(InvalidParameterException::new);
        redditSecret = Optional.ofNullable(System.getProperty("reddit.secret")).orElseThrow(InvalidParameterException::new);
    }


    @SparkGet("/login")
    public void redditLogin(Request req, Response res) {
        req.session(true);


        String random = UUID.randomUUID().toString();
        req.session().attribute(SESSION_LOGIN_STRING, random);

        String url = "https://www.reddit.com/api/v1/authorize.compact?client_id=" + redditClientId + "&response_type=code&state=" + random + "&redirect_uri=" + REDIRECT_URL + "&duration=temporary&scope=identity";

        res.redirect(url);
    }


    @SparkGet("/api/me")
    public String getUsername(Request req, Response res) {
        String username = req.session().attribute(SESSION_USERNAME);
        if (username != null) {
            return username;
        } else {
            res.status(401);
            return "Please Log in";
        }
    }

    @SparkGet("/post-login")
    public void postLogin(@SparkQueryParam("error") String error, @SparkQueryParam("state") String state, @SparkQueryParam("code") String code, Request req, Response res) throws UnirestException {
        if (error != null && error.equalsIgnoreCase("access_denied")) {
            res.redirect("/");
        }


        if (((String) req.session().attribute(SESSION_LOGIN_STRING)).equalsIgnoreCase(state)) {
            JsonNode body = Unirest.post("https://www.reddit.com/api/v1/access_token")
                    .basicAuth(redditClientId, redditSecret)
                    .header("User-Agent", REDDIT_USER_AGENT)
                    .field("grant_type", "authorization_code")
                    .field("code", code)
                    .field("redirect_uri", REDIRECT_URL)
                    .asJson().getBody();

            Optional<String> username = Optional.ofNullable(body.getObject().getString("access_token"))
                    .map(s ->
                                    //getting username
                            {
                                try {
                                    return Unirest.get("https://oauth.reddit.com/api/v1/me")
                                            .header("Authorization", "Bearer " + s)
                                            .header("User-Agent", REDDIT_USER_AGENT)
                                            .asJson()
                                            .getBody().getObject();
                                } catch (UnirestException e) {
                                    logger.error("Couldn't get user identity", e);
                                    return null;
                                }
                            }
                    ).map(s -> s.getJSONObject("subreddit"))
                    .map(s -> s.getString("display_name_prefixed"))
                    .filter(s -> s.matches("u/\\w+"))
                    .map(s -> "/" + s);

            if (username.isPresent()) {
                req.session().attribute(SESSION_USERNAME, username.get());
            }

            res.redirect("/me");


        } else {
            res.status(401);
        }

    }

    @SparkGet("/fighter/:fighter")
    public String serveIndex() throws URISyntaxException, IOException {
        URL index = HypeTrainController.class
                .getClassLoader().getResource("web/public/index.html");
        Path p = Paths.get(index.toURI());
        return Files.lines(p).collect(Collectors.joining());
    }


    @SparkGet("/me")
    public String me(Response res, Request req) throws IOException, URISyntaxException {

        String user = req.session().attribute(SESSION_USERNAME);
        if (user == null) {
            res.status(401);
            return "Please log in first";
        }

        return serveIndex();
    }

    @SparkPost(value = "/api/search", transformer = GsonTransformer.class)
    public List<MmathFighter> index(@SparkQueryParam("name") String name) {

        List<MmathFighter> mmathFighters = dao.getFighterDAO().searchByName(name);
        return mmathFighters;
    }


    @SparkGet(value = "/api/top", transformer = GsonTransformer.class)
    public List<AggregatedHypeTrain> getTopTrains() {
        return dao.getHypeTrainDAO().getTop();
    }

    @SparkGet(value = "/api/fighter/:fighter", transformer = GsonTransformer.class)
    public Map<String, Object> getFighter(@SparkParam("fighter") String hash, Request req) {

        MmathFighter fighter = dao.getFighterDAO().getFromHash(hash);

        String user = req.session().attribute(SESSION_USERNAME);


        Map<String, Object> data = new HashMap<>();
        data.put("id", fighter.getIdAsHash());
        data.put("name", fighter.getName());
        data.put("loggedIn", user != null);
        data.put("count", dao.getHypeTrainDAO().countForFighter(fighter.getSherdogUrl()));
        if (user != null) {
            data.put("onBoard", dao.getHypeTrainDAO().isOnBoard(user, fighter.getSherdogUrl()));
        } else {
            data.put("onBoard", false);
        }

        return data;
    }


    @SparkGet("/api/jumpOn/:fighter")
    public String jumpOn(@SparkParam("fighter") String hash, Response res, Request req) {
        MmathFighter fighter = dao.getFighterDAO().getFromHash(hash);

        String user = req.session().attribute(SESSION_USERNAME);

        if (user == null) {
            res.status(401);
            return "Please Log in";
        }

        if (fighter == null) {
            res.status(404);
            return "Fighter doesn't exist";
        }


        HypeTrain hypeTrain = new HypeTrain(user, fighter.getSherdogUrl());

        dao.getHypeTrainDAO().insert(hypeTrain);
        res.status(200);
        return "OK";
    }


    @SparkGet("/api/jumpOff/:fighter")
    public String jumpOff(@SparkParam("fighter") String hash, Response res, Request req) {
        MmathFighter fighter = dao.getFighterDAO().getFromHash(hash);

        String user = req.session().attribute(SESSION_USERNAME);

        if (user == null) {
            res.status(401);
            return "Please Log in";
        }

        if (fighter == null) {
            res.status(404);
            return "Fighter doesn't exists";
        }


        HypeTrain hypeTrain = new HypeTrain(user, fighter.getSherdogUrl());

        dao.getHypeTrainDAO().deleteById(hypeTrain);
        res.status(200);
        return "OK";
    }

    @SparkGet(value = "/api/my-hype", transformer = GsonTransformer.class)
    public List<HypeTrain> myHype(Request req, Response res) {

        String user = req.session().attribute(SESSION_USERNAME);

        if (user == null) {
            res.status(401);
            return Collections.emptyList();
        }

        return dao.getHypeTrainDAO().getByUser(user);
    }


    @SparkGet("/train/:people")
    public String getTrainForPeople(@SparkParam("people") int people, Response res) {
        res.header("Content-Type", "image/svg+xml");
        return TrainGenerator.withPeople(people);
    }
}
