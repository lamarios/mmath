package com.ftpix.hypetrain.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ftpix.hypetrain.web.GsonTransformer;
import com.ftpix.hypetrain.web.TrainGenerator;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.dao.mysql.HypeTrainDAO;
import com.ftpix.mmath.model.AggregatedHypeTrain;
import com.ftpix.mmath.model.HypeTrain;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sparknnotation.annotations.*;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import spark.Request;
import spark.Response;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@SparkController
@Component
public class HypeTrainController {

    private static final String SESSION_LOGIN_STRING = "loginString";
    private static final String REDDIT_USER_AGENT = "web:com.mmathypetrain:1.0 (by /u/lamarios)";
    private static final String SESSION_TOKEN = "token";
    private static final String JWT_TOKEN_ISSUER = "mmath";


    @Value("${HYPETRAIN_JWT_SALT:somesupersalt}")
    private String jwtSalt;

    @Value("${REDDIT_CLIENT_ID}")
    private String redditClientId;


    @Value("${REDDIT_SECRET}")
    private String redditSecret;



    @Value("${REDDIT_REDIRECT_URL:http://localhost:15679}")
    private String redditRedirectUrl;


    @Autowired
    private FighterDAO fighterDAO;

    @Autowired
    private HypeTrainDAO hypeTrainDAO;


    private Log logger = LogFactory.getLog(this.getClass());


    @PostConstruct
    public void setUrl(){
        redditRedirectUrl=redditRedirectUrl+"/post-login";
    }


    @SparkGet("/login")
    public void redditLogin(Request req, Response res) {
        req.session(true);


        String random = UUID.randomUUID().toString();
        req.session().attribute(SESSION_LOGIN_STRING, random);

        String url = "https://www.reddit.com/api/v1/authorize.compact?client_id=" + redditClientId + "&response_type=code&state=" + random + "&redirect_uri=" + redditRedirectUrl+ "&duration=temporary&scope=identity";

        res.redirect(url);
    }


    @SparkGet("/api/me")
    public String getUsername(Request req, Response res) {
        String username = getUserFromToken(req.session().attribute(SESSION_TOKEN));
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
                    .field("redirect_uri", redditRedirectUrl)
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
                Algorithm algorithmHS = Algorithm.HMAC256(jwtSalt);
                String token = JWT.create()
                        .withIssuer(JWT_TOKEN_ISSUER)
                        .withSubject(username.get())
                        .sign(algorithmHS);
                req.session().attribute(SESSION_TOKEN, token);
            }

            res.redirect("/me");


        } else {
            res.status(401);
        }

    }

    @SparkGet("/fighter/:fighter")
    public String serveIndex() throws URISyntaxException, IOException {
        try (
                InputStream inputStream = getClass().getClassLoader().getResource("web/public/index.html").openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            return reader.lines().collect(Collectors.joining(""));
        }
    }


    @SparkGet("/me")
    public String me(Response res, Request req) throws IOException, URISyntaxException {
        try {
            String user = getUserFromToken(req.session().attribute(SESSION_TOKEN));
            if (user == null) {
                res.status(401);
                return "Please log in first";
            }
        } catch (JWTVerificationException e) {
            res.status(401);
            return "Please log in first";
        }

        return serveIndex();
    }

    @SparkPost(value = "/api/search", transformer = GsonTransformer.class)
    public List<MmathFighter> index(@SparkQueryParam("name") String name) {

        List<MmathFighter> mmathFighters = fighterDAO.searchByName(name);
        return mmathFighters;
    }


    @SparkGet(value = "/api/top", transformer = GsonTransformer.class)
    public List<AggregatedHypeTrain> getTopTrains() {
        return hypeTrainDAO.getTop();
    }

    @SparkGet(value = "/api/fighter/:fighter", transformer = GsonTransformer.class)
    public Map<String, Object> getFighter(@SparkParam("fighter") String hash, Request req) {

        MmathFighter fighter = fighterDAO.getFromHash(hash);
        Map<String, Object> data = new HashMap<>();
        String user = null;
        try {
            user = getUserFromToken(req.session().attribute(SESSION_TOKEN));
        } catch (JWTVerificationException e) {
            //not logged in user remains null
        }


        data.put("id", fighter.getIdAsHash());
        data.put("name", fighter.getName());
        data.put("loggedIn", user != null);
        data.put("count", hypeTrainDAO.countForFighter(fighter.getSherdogUrl()));
        if (user != null) {
            data.put("onBoard", hypeTrainDAO.isOnBoard(user, fighter.getSherdogUrl()));
        } else {
            data.put("onBoard", false);
        }

        return data;
    }


    @SparkGet("/api/jumpOn/:fighter")
    public String jumpOn(@SparkParam("fighter") String hash, Response res, Request req) {
        MmathFighter fighter = fighterDAO.getFromHash(hash);

        String user = getUserFromToken(req.session().attribute(SESSION_TOKEN));

        if (user == null) {
            res.status(401);
            return "Please Log in";
        }

        if (fighter == null) {
            res.status(404);
            return "Fighter doesn't exist";
        }


        HypeTrain hypeTrain = new HypeTrain(user, fighter.getSherdogUrl());

        hypeTrainDAO.insert(hypeTrain);
        res.status(200);
        return "OK";
    }


    @SparkGet("/api/jumpOff/:fighter")
    public String jumpOff(@SparkParam("fighter") String hash, Response res, Request req) {
        MmathFighter fighter = fighterDAO.getFromHash(hash);

        String user = getUserFromToken(req.session().attribute(SESSION_TOKEN));

        if (user == null) {
            res.status(401);
            return "Please Log in";
        }

        if (fighter == null) {
            res.status(404);
            return "Fighter doesn't exists";
        }


        HypeTrain hypeTrain = new HypeTrain(user, fighter.getSherdogUrl());

        hypeTrainDAO.deleteById(hypeTrain);
        res.status(200);
        return "OK";
    }

    @SparkGet(value = "/api/my-hype", transformer = GsonTransformer.class)
    public List<HypeTrain> myHype(Request req, Response res) {

        String user = getUserFromToken(req.session().attribute(SESSION_TOKEN));

        if (user == null) {
            res.status(401);
            return Collections.emptyList();
        }

        return hypeTrainDAO.getByUser(user);
    }


    @SparkGet("/train/:people")
    public String getTrainForPeople(@SparkParam("people") int people, @SparkQueryParam("color") String color, Response res) {
        res.header("Content-Type", "image/svg+xml");
        color = color == null ? "#000" : "#" + color;
        return TrainGenerator.withPeople(people, color);
    }


    @SparkGet(value = "/api/history/:fighter", transformer = GsonTransformer.class)
    public Map<String, Long> getFighterHistory(@SparkParam("fighter") String fighter, @SparkQueryParam("count") int count) {
        if (count <= 5) {
            count = 5;
        }

        Function<LocalDate, String> dateToString = localDate -> localDate.getYear() + "-" + String.format("%02d", localDate.getMonthValue());

        Map<String, Long> results = new TreeMap<>();


        //Initializing date map
        LocalDate date = LocalDate.now();

        for (int i = 0; i < count; i++) {
            results.put(dateToString.apply(date.minusMonths(i)), 0L);
        }

        hypeTrainDAO.getStats(fighter, count)
                .stream()
                .forEach(s -> results.put(s.getMonth(), s.getCount()));


        return results;
    }

    private String getUserFromToken(String token) throws JWTVerificationException {
        if (token == null) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(jwtSalt);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(JWT_TOKEN_ISSUER)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
