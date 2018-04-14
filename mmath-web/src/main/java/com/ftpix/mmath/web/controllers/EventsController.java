package com.ftpix.mmath.web.controllers;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;
import java.util.stream.Collectors;

public class EventsController implements Controller {
    private final MySQLDao dao;
    private Logger logger = LogManager.getLogger();
    private final Gson gson = GsonUtils.getGson();

    public EventsController(MySQLDao dao) {
        this.dao = dao;
    }


    @Override
    public void declareEndPoints() {
        Spark.get("/api/events/incoming", this::getIncomingEvents, gson::toJson);
        Spark.get("/api/events/organization-filters", this::getOrganizationFilter, gson::toJson);
        Spark.get("/api/events/:id/fights", this::getEventFights, gson::toJson);
        Spark.get("/api/events/:id", this::getEvent, gson::toJson);
    }

    /**
     * Gets all the orgs that should appear in the search filter
     * @param request
     * @param response
     * @return
     */
    private List<MmathOrganization> getOrganizationFilter(Request request, Response response) {
        return dao.getOrganizationDAO().getOrganizationsInEventFilter();
    }

    private MmathEvent getEvent(Request request, Response response) {
        return dao.getEventDAO().getFromHash(request.params(":id"));
    }

    private List<MmathFight> getEventFights(Request request, Response response) {
        return dao.getFightDAO().getFightsForEventHash(request.params(":id")).stream()
                .map(f -> {
                    f.setFighter1(dao.getFighterDAO().getById(f.getFighter1().getSherdogUrl()));
                    f.setFighter2(dao.getFighterDAO().getById(f.getFighter2().getSherdogUrl()));
                    return f;
                }).collect(Collectors.toList());
    }

    /**
     * Get all the incoming events
     *
     * @param request
     * @param response
     * @return
     */
    private List<MmathEvent> getIncomingEvents(Request request, Response response) {
        return dao.getEventDAO().getIncoming().stream()
                .map(e-> {
                    e.setOrganization(dao.getOrganizationDAO().getById(e.getOrganization().getSherdogUrl()));
                    return e;
                }).collect(Collectors.toList());
    }


}
