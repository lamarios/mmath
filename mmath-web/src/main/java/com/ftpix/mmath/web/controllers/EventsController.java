package com.ftpix.mmath.web.controllers;

import com.ftpix.mmath.dao.mysql.EventDAO;
import com.ftpix.mmath.dao.mysql.FightDAO;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.dao.mysql.OrganizationDAO;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class EventsController implements Controller {
    private Logger logger = LogManager.getLogger();
    private final Gson gson = GsonUtils.getGson();

    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private FighterDAO fighterDAO;

    @Override
    public void declareEndPoints() {
        Spark.get("/api/events/incoming", this::getIncomingEvents, gson::toJson);
        Spark.get("/api/events/organization-filters", this::getOrganizationFilter, gson::toJson);
        Spark.get("/api/events/:id/fights", this::getEventFights, gson::toJson);
        Spark.get("/api/events/:id", this::getEvent, gson::toJson);
    }

    /**
     * Gets all the orgs that should appear in the search filter
     *
     * @param request
     * @param response
     * @return
     */
    private List<MmathOrganization> getOrganizationFilter(Request request, Response response) {
        return organizationDAO.getOrganizationsInEventFilter();
    }

    private MmathEvent getEvent(Request request, Response response) {
        return eventDAO.getFromHash(request.params(":id"));
    }

    private List<MmathFight> getEventFights(Request request, Response response) {
        return fightDAO.getFightsForEventHash(request.params(":id")).stream()
                .map(f -> {
                    f.setFighter1(fighterDAO.getById(f.getFighter1().getSherdogUrl()));
                    f.setFighter2(fighterDAO.getById(f.getFighter2().getSherdogUrl()));
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
        String organizations = request.queryParams("organizations");
        int page = 1;

        try {
            page = Integer.parseInt(request.queryParams("page"));
        } catch (Exception e) {

        }

        return eventDAO.getIncoming(organizations, page).stream()
                .map(e -> {
                    e.setOrganization(organizationDAO.getById(e.getOrganization().getSherdogUrl()));
                    return e;
                }).collect(Collectors.toList());


    }
}
