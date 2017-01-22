package com.ftpix.calculator.web;

import com.google.gson.Gson;

import com.ftpix.calculator.BetterThan;
import com.ftpix.calculator.BetterWeakerThanCount;
import com.ftpix.mmath.dao.FighterDao;
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
    private final BetterWeakerThanCount betterWeakerThanCount;
    private final int port;
    private final FighterDao dao;
    private final Logger logger = LogManager.getLogger();

    private final static Gson gson = GsonUtils.getGson();

    public WebServer(BetterThan betterThan, BetterWeakerThanCount betterWeakerThanCount, FighterDao dao, int port) {
        this.betterThan = betterThan;
        this.betterWeakerThanCount = betterWeakerThanCount;
        this.port = port;
        this.dao = dao;
    }

    public void setupServer() {
        Spark.port(this.port);
        Spark.get("/", "application/json", this::hello, gson::toJson);
        Spark.get("/better-than/:fighter/count", "application/json", this::betterThanCountEndPoint, gson::toJson);
        Spark.get("/weaker-than/:fighter/count", "application/json", this::weakerThanCountEndPoint, gson::toJson);
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
        Optional<MmathFighter> fighter1 = dao.get(req.params(":fighter1"));
        Optional<MmathFighter> fighter2 = dao.get(req.params(":fighter2"));

        if (fighter1.isPresent() && fighter2.isPresent()) {
            return betterThan.find(fighter1.get().getSherdogUrl(), fighter2.get().getSherdogUrl());
        } else {
            Spark.halt(404, "Fighter don't exist in DB");
            return null;
        }
    }

    public long betterThanCountEndPoint(Request req, Response res) {
        return count(req, BetterWeakerThanCount.Type.BETTER_THAN);
    }

    public Object weakerThanCountEndPoint(Request req, Response res) {
        return count(req, BetterWeakerThanCount.Type.WEAKER_THAN);
    }


    private Long count(Request req, BetterWeakerThanCount.Type type) {
        Optional<MmathFighter> fighter = dao.get(req.params(":fighter"));

        if (fighter.isPresent()) {
            return betterWeakerThanCount.count(fighter.get().getSherdogUrl(), type);
        } else {
            Spark.halt(404, "Fighter don't exist in DB");
            return -1L;
        }
    }
}
