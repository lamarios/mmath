package com.ftpix.calculator.web;

import com.google.gson.Gson;

import com.ftpix.calculator.BetterThan;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.utils.GsonUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import spark.Request;
import spark.Response;
import spark.Spark;


/**
 * Created by gz on 24-Sep-16.
 */
public class WebServer {
    private final BetterThan betterThan;
    private final int port;
    private final Logger logger = LogManager.getLogger();

    private final Dao<MmathFighter, String> fighterDao;
    private final static Gson gson = GsonUtils.getGson();

    public WebServer(BetterThan betterThan, int port, Dao<MmathFighter, String> fighterDao) {
        this.betterThan = betterThan;
        this.port = port;
        this.fighterDao = fighterDao;
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

    public List<MmathFighter> betterThanEndpoint(Request req, Response res) throws SQLException {
        Optional<MmathFighter> fighter1 = getFighterFromHash(req.params(":fighter1"));
        Optional<MmathFighter> fighter2 = getFighterFromHash(req.params(":fighter2"));

        if (fighter1.isPresent() && fighter2.isPresent()) {
            return betterThan.findJDBC(fighter1.get(), fighter2.get())
                    .stream()
                    .map(f -> {
                        try {
                            return fighterDao.queryForId(f);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            Spark.halt(404, "Fighter don't exist in DB");
            return null;
        }
    }


    /**
     * Gets a fighter from its url hash
     *
     * @param hash
     * @return
     * @throws SQLException
     */
    private Optional<MmathFighter> getFighterFromHash(String hash) throws SQLException {
        PreparedQuery<MmathFighter> query = fighterDao.queryBuilder().where().raw("MD5(sherdogUrl) = ?", new SelectArg(SqlType.STRING, hash)).prepare();
        return fighterDao.query(query).stream().findFirst();

    }
}
