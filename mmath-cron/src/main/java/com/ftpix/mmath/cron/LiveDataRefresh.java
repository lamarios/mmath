package com.ftpix.mmath.cron;


import com.ftpix.mmath.cron.graph.GraphGenerator;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.dao.mysql.EventDAO;
import com.ftpix.mmath.dao.mysql.FightDAO;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;
import com.ftpix.sherdogparser.parsers.ParserUtils;
import com.ftpix.webwatcher.WebWatcher;
import com.ftpix.webwatcher.interfaces.WebSiteListener;
import com.orientechnologies.orient.core.exception.OCoreException;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
@EnableScheduling
public class LiveDataRefresh implements WebSiteListener<MmathEvent> {
    private Logger logger = LogManager.getLogger();

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
                        fightDAO.deleteExistingSimilarFight(f.getFighter1().getSherdogUrl(), f.getFighter2().getSherdogUrl(), event.getSherdogUrl());
                        f.setId(fightDAO.insert(f));
                        try {
                            graphGenerator.addFightToGraph(f, graph);
                        } catch (OCoreException e) {
                            logger.info("Fight {} vs {} for event {} probably already exists, {}", f.getFighter1().getName(), f.getFighter2().getName(), event.getName(), e.getMessage());
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
}
