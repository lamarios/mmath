package com.ftpix.mmath.web.controllers;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.GsonFriendlyFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.web.models.Query;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MmathController implements Controller {

    private final MySQLDao dao;
    private final OrientDBDao orientDBDao;
    private Logger logger = LogManager.getLogger();
    private final Gson gson = GsonUtils.getGson();

    public MmathController(MySQLDao dao, OrientDBDao orientDBDao) {
        this.dao = dao;
        this.orientDBDao = orientDBDao;
    }

    @Override
    public void declareEndPoints() {

        Spark.get("/api/better-than/:fighter1/:fighter2", "application/json", this::betterThan, gson::toJson);
        Spark.post("/api/fighters/query", "application/json", this::searchFighter, gson::toJson);
        Spark.get("/api/fighter/:id", "application/json", this::getFighter, gson::toJson);
        Spark.get("/api/fights/:id", "application/json", this::getFights, gson::toJson);
    }


    /**
     * Gets a single fighter from its hash
     * @param request
     * @param response
     * @return
     * @throws SQLException
     */
    private MmathFighter getFighter(Request request, Response response) throws SQLException {

        return Optional.ofNullable(dao.getFighterDAO().getFromHash(request.params(":id"))).get();
    }


    /**
     * Search fights by name or nickname
     * @param request
     * @param response
     * @return
     * @throws InterruptedException
     */
    private List<MmathFighter> searchFighter(Request request, Response response) throws InterruptedException {
        Query query = gson.fromJson(request.body(), Query.class);
        return Optional.ofNullable(dao.getFighterDAO().searchByName(query.getName())).orElse(new ArrayList<>());

    }


    /**
     * Do the tree search query both ways through fighter 1 and 2
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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

                if (f1.getWins() == 0 || f2.getLosses() == 0) {
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
}
