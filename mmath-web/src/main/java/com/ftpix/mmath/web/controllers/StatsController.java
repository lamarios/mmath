package com.ftpix.mmath.web.controllers;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;
import java.util.stream.Collectors;

public class StatsController implements Controller {
    private final MySQLDao dao;
    private final Gson gson = GsonUtils.getGson();

    public StatsController(MySQLDao dao) {
        this.dao = dao;
    }

    @Override
    public void declareEndPoints() {
        Spark.get("/api/stats", this::getCategories, gson::toJson);
        Spark.get("/api/stats/entries/:cat", this::getStatsForCategory, gson::toJson);
        Spark.get("/api/stats/:cat", this::getCategory, gson::toJson);
        Spark.get("/api/stats/for-fighter/:hash", this::getFighterStats, gson::toJson);
    }

    /**
     * Gets the stats of a single fighter
     * @param request
     * @param response
     * @return
     */
    private List<StatsEntry> getFighterStats(Request request, Response response) {
        return dao.getStatsEntryDAO().getForFighterHash(request.params(":hash"))
                .stream()
                .map(s -> {
                    s.setCategory(dao.getStatsCategoryDAO().getById(s.getCategory().getId()));
                    return s;
                }).collect(Collectors.toList());
    }

    /**
     * Gets a single category
     *
     * @param request
     * @param response
     * @return
     */
    private StatsCategory getCategory(Request request, Response response) {

        return dao.getStatsCategoryDAO().getById(request.params(":cat"));
    }

    /**
     * Gets stats for a particular category
     *
     * @param request
     * @param response
     * @return a list of stats entries
     */
    private List<StatsEntry> getStatsForCategory(Request request, Response response) {
        String cat = request.params(":cat");

        return dao.getStatsEntryDAO().getByCategory(cat).stream()
                .map(s -> {
                    s.setFighter(dao.getFighterDAO().getById(s.getFighter().getSherdogUrl()));
                    return s;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets all the available stats categories
     *
     * @param request
     * @param response
     * @return the list of categories.
     */
    private List<StatsCategory> getCategories(Request request, Response response) {

        return dao.getStatsCategoryDAO().getAll();
    }
}
