package com.ftpix.mmath.web;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.GsonFriendlyFight;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.web.models.Query;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import mmath.S3Helper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by gz on 26-Sep-16.
 */
public class WebServer {

    private final int port;

    private final Dao<MmathFighter, String> fighterDao;
    private final Dao<MmathFight, Long> fightDao;
    private final Dao<MmathEvent, String> eventDao;
    private final OrientDBDao orientDBDao;
    private final S3Helper s3Helper;

    private Logger logger = LogManager.getLogger();
    private final Gson gson = GsonUtils.getGson();

    public WebServer(int port, Dao<MmathFighter, String> fighterDao, Dao<MmathFight, Long> fightDao, Dao<MmathEvent, String> eventDao, OrientDBDao orientDBDao, S3Helper s3Helper) {
        this.port = port;
        this.fighterDao = fighterDao;
        this.fightDao = fightDao;
        this.eventDao = eventDao;
        this.orientDBDao = orientDBDao;
        this.s3Helper = s3Helper;
    }

    public void startServer() {
        Spark.port(port);

        if (System.getProperty("dev", "false").equalsIgnoreCase("true")) {
            logger.info("DEV MODE");
            Spark.externalStaticFileLocation("/home/gz/IdeaProjects/mmath/mmath-web/src/main/resources/web/public");

        } else {
            Spark.staticFiles.location("/web/public");
        }

        Spark.before("*", this::logRequest);
        Spark.before("/api/*", this::jsonRequest);
        Spark.get("/api/better-than/:fighter1/:fighter2", "application/json", this::betterThan, gson::toJson);
        Spark.post("/api/fighters/query", "application/json", this::searchFighter, gson::toJson);
        Spark.get("/api/fighters/:id", "application/json", this::getFighter, gson::toJson);
        Spark.get("/pictures/*", this::getFighterPicture);
    }

    /**
     * Get an image from the cache path
     */
    private Object getFighterPicture(Request request, Response response) throws IOException {

        String fighterHash = request.splat()[0];


        response.raw().setContentType("application/octet-stream");
        response.raw().setHeader("Content-Disposition", "inline; filename=" + fighterHash);
        try (S3ObjectInputStream in = s3Helper.getFile(fighterHash)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                response.raw().getOutputStream().write(buffer, 0, len);
            }

            return response.raw();
        }
    }

    private MmathFighter getFighter(Request request, Response response) throws SQLException {

        return getFighterFromHash(request.params(":id")).get();
    }

    private void jsonRequest(Request request, Response response) {
        response.type("application/json");
    }

    private List<MmathFighter> searchFighter(Request request, Response response) {
        try {
            Query query = gson.fromJson(request.body(), Query.class);
            PreparedQuery<MmathFighter> like = fighterDao.queryBuilder()
                    .limit(5l)
                    .where()
                    .like("name", "%" + query.getName() + "%")
                    .prepare();
            ;
            List<MmathFighter> fighters = fighterDao.query(like);


            List<MmathFighter> results = fighters.stream().map(fighter -> {
                try {
                   // setFighterFights(fighter);
                    return fighter;
                } catch (Exception e) {
                    logger.error("Couldn't get fighter's fights", e);
                    return null;
                }
            }).collect(Collectors.toList());

            return results;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    private List<MmathFighter> betterThan(Request request, Response response) throws IOException {
        try {
            String fighter1 = request.params(":fighter1");
            String fighter2 = request.params(":fighter2");


            logger.info("{} vs {}", fighter1, fighter2);

            Optional<MmathFighter> fighter1Opt = getFighterFromHash(fighter1);
            Optional<MmathFighter> fighter2Opt = getFighterFromHash(fighter2);

            if (fighter1Opt.isPresent() && fighter2Opt.isPresent()) {
                List<MmathFighter> result = orientDBDao.findShortestPath(fighter1Opt.get(), fighter2Opt.get())
                        .stream()
                        .map(f -> {
                            try {
                                MmathFighter fighter = fighterDao.queryForId(f);
                                //setFighterFights(fighter);
                                return fighter;
                            } catch (SQLException e) {
                                return null;
                            }
                        })
                        .collect(Collectors.toList());

                logger.info("Result size: {}", result.size());
                return result;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("error: ", e);
            return new ArrayList<>();
        }
    }

    private void logRequest(Request request, Response response) {
        logger.info("{} {}", request.requestMethod(), request.url());

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

    /* private void setFighterFights(MmathFighter fighter) throws SQLException {

        QueryBuilder<MmathFight, Long> query = fightDao.queryBuilder();
        Where<MmathFight, Long> where = query.where();

        PreparedQuery<MmathFight> prepare = where.or(
                where.eq("fighter1_id", fighter.getSherdogUrl())
                ,
                where.eq("fighter2_id", fighter.getSherdogUrl())
        ).prepare();

        List<GsonFriendlyFight> fights = fightDao.query(prepare).stream()
                .map(f -> {
                    GsonFriendlyFight gsonFight = new GsonFriendlyFight();
                    gsonFight.setDate(f.getEvent().getDate());
                    //we need to swap
                    if (f.getFighter2().getSherdogUrl().equalsIgnoreCase(fighter.getSherdogUrl())) {
                        f.setFighter2(f.getFighter1());
                        f.setFighter1(fighter);
                        switch (f.getResult()) {
                            case FIGHTER_1_WIN:
                                f.setResult(FightResult.FIGHTER_2_WIN);
                                break;
                            case FIGHTER_2_WIN:
                                f.setResult(FightResult.FIGHTER_1_WIN);
                                break;
                        }
                    }

                    gsonFight.setResult(f.getResult());
                    gsonFight.setOpponent(f.getFighter2().getName());
                    gsonFight.setEvent(f.getEvent().getName());
                    gsonFight.setWinMethod(f.getWinMethod());
                    gsonFight.setWinRound(f.getWinRound());
                    gsonFight.setWinTime(f.getWinTime());

                    return gsonFight;
                })
                .sorted(Comparator.comparing(f -> f.getDate()))
                .collect(Collectors.toList());

        fighter.setGsonFriendlyFights(fights);

    } */
}
