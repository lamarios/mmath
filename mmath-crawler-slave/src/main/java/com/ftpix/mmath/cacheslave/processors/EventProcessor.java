package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.FightType;
import com.ftpix.sherdogparser.parsers.ParserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by gz on 16-Sep-16.
 */
@Component
public class EventProcessor extends Processor<MmathEvent> {

    private final ExecutorService exec = Executors.newFixedThreadPool(5);



    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;


    @Override
    protected void propagate(MmathEvent event) {
        event.getFights().forEach(f -> {
            jmsTemplate.convertAndSend(fighterTopic, f.getFighter1().getSherdogUrl());
            jmsTemplate.convertAndSend(fighterTopic, f.getFighter2().getSherdogUrl());
            insertFight(f);
        });

        jmsTemplate.convertAndSend(organizationTopic, event.getOrganization().getSherdogUrl());
    }

    private void insertFight(MmathFight f) {
        try {
            String fighter1 = Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).orElse("");
            String fighter2 = Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).orElse("");

            //deleting any existing similar fights, so we can insert it again, as sometimes sherdog changes the order of  fighter1 / fighter2
            fightDAO.deleteExistingSimilarFight(fighter1, fighter2, f.getEvent().getSherdogUrl());
            fightDAO.replace(f);
        } catch (DuplicateKeyException e) {
            logger.info("Fight "+f.getFighter1().getSherdogUrl()+" vs "+f.getFighter2().getSherdogUrl()+"  at event "+f.getEvent().getSherdogUrl()+" already exists");
        }
    }

    @Override
    protected void insertToDao(MmathEvent event) throws SQLException {
        try {
            eventDAO.insert(event);
        } catch (DuplicateKeyException e) {
            logger.info("Event already exist, skipping insert");
        }
    }

    @Override
    protected void updateToDao(MmathEvent old, MmathEvent event) throws SQLException {
        eventDAO.update(event);
    }

    @Override
    protected MmathEvent getFromSherdog(String url) throws IOException, ParseException, SherdogParserException {
        Event event = sherdog.getEvent(url);
        MmathEvent e = MmathEvent.fromSherdog(event);
        e.setFights(new ArrayList<>());


        //Getting all the fight types
        List<Callable<Void>> tasks = event.getFights()
                .stream()
                .map(f ->
                        (Callable<Void>) () -> {
                            FightType type = ParserUtils.getFightType(sherdog, f);
                            f.setType(type);
                            return null;
                        }
                )
                .collect(Collectors.toList());

        try {
            exec.invokeAll(tasks);
        } catch (InterruptedException e1) {
            logger.error("Couldn't get fight types", e1);
        }

        event.getFights().forEach(f -> {
            e.getFights().add(MmathFight.fromSherdog(f));
        });

        return e;
    }

    @Override
    protected LocalDateTime getLastUpdate(MmathEvent event) {
        return event.getLastUpdate();
    }

    @Override
    protected Optional<MmathEvent> getFromDao(String url) throws SQLException {
        return Optional.ofNullable(eventDAO.getById(url));
    }
}

