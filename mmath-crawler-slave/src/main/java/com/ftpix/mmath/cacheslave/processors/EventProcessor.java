package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Event;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class EventProcessor extends Processor<MmathEvent> {
    private final Dao<MmathEvent, String> eventDao;
    private final Dao<MmathFight, Long> fightDao;

    public EventProcessor(Receiver receiver, Dao<MmathEvent, String> eventDao, Sherdog sherdog, Dao<MmathFight, Long> fightDao) {
        super(receiver, sherdog);
        this.eventDao = eventDao;
        this.fightDao = fightDao;
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
        //TODO insert fight
        try {
            fightDao.createIfNotExists(f);
        }catch (SQLException e){
            logger.info("Fight {} vs {}  at event {} already exists", f.getFighter1().getSherdogUrl(), f.getFighter2().getSherdogUrl(), f.getEvent().getSherdogUrl());
        }
    }

    @Override
    protected void insertToDao(MmathEvent event) throws SQLException {
        eventDao.createOrUpdate(event);
    }

    @Override
    protected void updateToDao(MmathEvent old, MmathEvent event) throws SQLException {
        event.setLastUpdate(new Date());


        eventDao.update(event);
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
    protected Date getLastUpdate(MmathEvent event) {
        return event.getLastUpdate();
    }

    @Override
    protected Optional<MmathEvent> getFromDao(String url) throws SQLException {
        return Optional.ofNullable(eventDao.queryForId(url));
    }
}

