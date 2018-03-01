package com.ftpix.mmath.web;

import com.amazonaws.services.s3.model.FileHeaderInfo;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.GsonFriendlyFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.web.models.Query;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import mmath.S3Helper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by gz on 26-Sep-16.
 */
public class WebServer {

    private final int port;

    private final MySQLDao dao;
    private final OrientDBDao orientDBDao;
    private final S3Helper s3Helper;

    private Logger logger = LogManager.getLogger();
    private final Gson gson = GsonUtils.getGson();


    public WebServer(int port, MySQLDao dao, OrientDBDao orientDBDao, S3Helper s3Helper) {
        this.port = port;
        this.dao = dao;
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
        Spark.get("/api/fights/:id", "application/json", this::getFights, gson::toJson);
        Spark.get("/pictures/*", this::getFighterPicture);
        Spark.exception(Exception.class, (e, request, response) -> {
            logger.error("Error while processing request", e);
            response.status(503);
            response.body(e.getMessage());
        });
    }

    /**
     * Returns a list of fights for a fighter
     *
     * @param request
     * @param response
     * @return
     */
    private List<GsonFriendlyFight> getFights(Request request, Response response) {

        String fighter1 = request.params(":id");

        return Optional.ofNullable(dao.getFighterDAO().getFromHash(fighter1))
                .map(fighter -> {
                    fighter.setFights(dao.getFightDAO().getByFighter(fighter.getSherdogUrl()));
                    fighter.getFights().forEach(f -> {
                        f.setEvent(dao.getEventDAO().getById(f.getEvent().getSherdogUrl()));
                        f.setFighter2(dao.getFighterDAO().getById(f.getFighter2().getSherdogUrl()));
                    });

                    return fighter.getGsonFriendlyFights();
                }).orElse(new ArrayList<>());
    }

    /**
     * Get an image from the cache path
     */
    private Object getFighterPicture(Request request, Response response) throws Exception {

        String fighterHash = request.splat()[0];
        response.raw().setContentType("application/octet-stream");
        response.raw().setHeader("Content-Disposition", "inline; filename=" + fighterHash);

        InputStream in;
        if (fighterHash.equalsIgnoreCase("default.jpg")) {
            File file = new File(getClass().getClassLoader().getResource("web/public/images/fighterPlaceHolder.gif").getFile());
            in = new FileInputStream(file);

        } else {
            in = s3Helper.getFile(fighterHash);
        }

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                response.raw().getOutputStream().write(buffer, 0, len);
            }

            return response.raw();

        } finally {
            in.close();
        }
    }

    private MmathFighter getFighter(Request request, Response response) throws SQLException {

        return Optional.ofNullable(dao.getFighterDAO().getFromHash(request.params(":id"))).get();
    }

    private void jsonRequest(Request request, Response response) {
        response.type("application/json");
    }

    private List<MmathFighter> searchFighter(Request request, Response response) throws InterruptedException {
        Query query = gson.fromJson(request.body(), Query.class);
        return Optional.ofNullable(dao.getFighterDAO().searchByName(query.getName())).orElse(new ArrayList<>());

    }


    private List<MmathFighter> betterThan(Request request, Response response) throws IOException {
        try {
            String fighter1 = request.params(":fighter1");
            String fighter2 = request.params(":fighter2");


            logger.info("{} vs {}", fighter1, fighter2);


            Optional<MmathFighter> fighter1Opt = Optional.ofNullable(dao.getFighterDAO().getFromHash(fighter1));
            Optional<MmathFighter> fighter2Opt = Optional.ofNullable(dao.getFighterDAO().getFromHash(fighter2));


            if (fighter1Opt.isPresent() && fighter2Opt.isPresent()) {

                // cutting short uselss long tree parsing
                MmathFighter f1 = fighter1Opt.get();
                MmathFighter f2 = fighter2Opt.get();

                if (f1.getWins() == 0 || f1.getLosses() == 0) {
                    return new ArrayList<>();
                }

                List<MmathFighter> result = orientDBDao.findShortestPath(f1, f2)
                        .stream()
                        .filter(f -> f != null)
                        .map(f -> {
//                            try {
                            final MmathFighter fighter = dao.getFighterDAO().getById(f);

                            return fighter;
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
