package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Event;
import org.springframework.dao.DuplicateKeyException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class EventProcessor extends Processor<MmathEvent> {

    private final MySQLDao dao;

    public EventProcessor(Receiver receiver, MySQLDao dao, Sherdog sherdog) {
        super(receiver, sherdog);

        this.dao = dao;
    }


    @Override
    protected void propagate(MmathEvent event) {
        event.getFights().forEach(f -> {
            receiver.process(new ProcessItem(f.getFighter1().getSherdogUrl(), ProcessType.FIGHTER));
            receiver.process(new ProcessItem(f.getFighter2().getSherdogUrl(), ProcessType.FIGHTER));
            insertFight(f);
        });

        receiver.process(new ProcessItem(event.getOrganization().getSherdogUrl(), ProcessType.ORGANIZATION));
    }

    private void insertFight(MmathFight f) {
        try {
            String fighter1 = Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).orElse("");
            String fighter2 = Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).orElse("");

            //deleting any existing similar fights, so we can insert it again, as sometimes sherdog changes the order of  fighter1 / fighter2
            dao.getFightDAO().deleteExistingSimilarFight(fighter1, fighter2, f.getEvent().getSherdogUrl());
            dao.getFightDAO().replace(f);
        } catch (DuplicateKeyException e) {
            logger.info("Fight {} vs {}  at event {} already exists", f.getFighter1().getSherdogUrl(), f.getFighter2().getSherdogUrl(), f.getEvent().getSherdogUrl());
        }
    }

    @Override
    protected void insertToDao(MmathEvent event) throws SQLException {
        try {
            dao.getEventDAO().insert(event);
        } catch (DuplicateKeyException e) {
            logger.info("Event already exist, skipping insert");
        }
    }

    @Override
    protected void updateToDao(MmathEvent old, MmathEvent event) throws SQLException {
        dao.getEventDAO().update(event);
    }

    @Override
    protected MmathEvent getFromSherdog(String url) throws IOException, ParseException {
        Event event = sherdog.getEvent(url);
        MmathEvent e = MmathEvent.fromSherdog(event);
        e.setFights(new ArrayList<>());
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
        return Optional.ofNullable(dao.getEventDAO().getById(url));
    }
}

