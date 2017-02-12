package com.ftpix.calculator.web;

import com.google.gson.Gson;

import com.ftpix.calculator.BetterThan;
import com.ftpix.mmath.caching.FighterCache;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.utils.GsonUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import spark.Request;
import spark.Response;
import spark.Spark;


/**
 * Created by gz on 24-Sep-16.
 */
public class WebServer {
    private final BetterThan betterThan;
    private final int port;
    private final FighterCache cache;
    private final Logger logger = LogManager.getLogger();

    private final static Gson gson = GsonUtils.getGson();

    public WebServer(BetterThan betterThan, FighterCache fighterCache, int port) {
        this.betterThan = betterThan;
        this.port = port;
        this.cache = fighterCache;
    }

    public void setupServer() {
        Spark.port(this.port);
        Spark.get("/", "application/json", this::hello, gson::toJson);
        Spark.get("/better-than/:fighter1/:fighter2", "application/json", this::betterThanEndpoint, gson::toJson);


        Spark.before(this::logRequest);

        Spark.exception(Exception.class, (e, request, response) -> {
            logger.error("Error while processing request", e);
            Spark.halt(500, "Error while processing request: " + e.getMessage());
        });
    }


    private void logRequest(Request req, Response res) {
        res.type("application/json");
        logger.info("{} {}", req.requestMethod(), req.url());
    }

    public Map<String, Object> hello(Request req, Response res) throws UnknownHostException {

        Map<String, Object> response = new HashMap<>();
        response.put("host", InetAddress.getLocalHost().getHostName());
        response.put("port", port);
        return response;
    }

    public List<MmathFighter> betterThanEndpoint(Request req, Response res) {
        Optional<MmathFighter> fighter1 = cache.get(req.params(":fighter1"));
        Optional<MmathFighter> fighter2 = cache.get(req.params(":fighter2"));

        if (fighter1.isPresent() && fighter2.isPresent()) {
            return betterThan.find(fighter1.get(), fighter2.get());
        } else {
            Spark.halt(404, "Fighter don't exist in DB");
            return null;
        }
    }
}
