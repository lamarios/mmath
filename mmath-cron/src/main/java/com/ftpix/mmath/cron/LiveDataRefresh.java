package com.ftpix.mmath.cron;


import com.ftpix.mmath.cron.graph.GraphGenerator;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.dao.mysql.EventDAO;
import com.ftpix.mmath.dao.mysql.FightDAO;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.parsers.ParserUtils;
import com.ftpix.webwatcher.WebWatcher;
import com.ftpix.webwatcher.interfaces.WebSiteListener;
import com.orientechnologies.orient.core.exception.OCoreException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class LiveDataRefresh implements WebSiteListener<MmathEvent> {
    protected Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;

    @Autowired
    private FighterDAO fighterDAO;


    @Autowired
    private GraphGenerator graphGenerator;

    @Autowired
    private OrientDBDao orientDb;


    private Sherdog sherdog = new Sherdog.Builder().build();

    @Scheduled(cron = "0 0 * * * *")
    public void watchEvents() {
        List<MmathEvent> events = eventDAO.getEventsToCheck();
        logger.info("Starting event content check");

        WebWatcher<MmathEvent> watcher = WebWatcher.watch(events.toArray(new MmathEvent[events.size()]))
                .triggerEventOnFirstCheck(true)
                .textOnly(false)
                .onChange(this);

        watcher.check();

    }

    @Override
    public void onContentChange(MmathEvent event, List<String> newContent, String pageNewHtml) {
        final OrientGraph graph = orientDb.getGraph();
        try {
            eventDAO.update(event);
            Event updatedEvent = sherdog.getEvent(Sherdog.BASE_URL + event.getSherdogUrl());
            updatedEvent.getFights()
                    .stream()
                    .map(f -> {
                        FightType fightType = ParserUtils.getFightType(sherdog, f);
                        f.setType(fightType);
                        return f;
                    })
                    .map(MmathFight::fromSherdog)
                    .filter(f -> f.getResult() != FightResult.NOT_HAPPENED)
                    .forEach(f -> {
                        MmathFighter fighter1 = getUpdatedFighter(f.getFighter1().getSherdogUrl());
                        MmathFighter fighter2 = getUpdatedFighter(f.getFighter2().getSherdogUrl());

                        if (fighter1 != null) fighterDAO.update(fighter1);
                        if (fighter2 != null) fighterDAO.update(fighter2);


                        fightDAO.deleteExistingSimilarFight(f.getFighter1().getSherdogUrl(), f.getFighter2().getSherdogUrl(), event.getSherdogUrl());
                        f.setId(fightDAO.insert(f));
                        try {
                            graphGenerator.addFightToGraph(f, graph);
                        } catch (OCoreException e) {
                            logger.info("Fight " + f.getFighter1().getName() + " vs " + f.getFighter2().getName() + " for event " + event.getName() + " probably already exists, " + e.getMessage());
                        }
                    });

            // we need to stop if all the fights havee a result;
            long count = updatedEvent.getFights().stream().filter(f -> f.getResult() != FightResult.NOT_HAPPENED).count();

            if (count == updatedEvent.getFights().size()) {
                //we reset the web watcher data so it won't be picked up again
                event.setLastCheck(null);
                event.setLastContentHash(null);
                eventDAO.update(event);
            }

        } catch (Exception e) {
            logger.error("Couldn't update fight", e);
        } finally {
            graph.shutdown();
        }
    }


    private MmathFighter getUpdatedFighter(String sherdogUrl) {
        return Optional.ofNullable(sherdogUrl)
                .map(fighterDAO::getById)
                .map(fighter -> {
                    try {
                        Fighter sherdogFighter = sherdog.getFighter(Sherdog.BASE_URL + fighter.getSherdogUrl());
                        MmathFighter updatedFighter = MmathFighter.fromSherdong(sherdogFighter);
                        updatedFighter.setSearchRank(fighter.getSearchRank());

                        return updatedFighter;
                    } catch (IOException | ParseException | SherdogParserException e) {
                        logger.error("Couldn't get fighter", e);
                        return null;
                    }
                })
                .orElseGet(() -> {
                    try {
                        MmathFighter mmathFighter = MmathFighter.fromSherdong(sherdog.getFighter(Sherdog.BASE_URL + sherdogUrl));
                        fighterDAO.insert(mmathFighter);
                        return mmathFighter;
                    } catch (IOException | ParseException | SherdogParserException e) {
                        logger.error("Couldn't get fighter", e);
                        return null;
                    }
                });
    }
}
