package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Event;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class EventProcessor extends Processor<MmathEvent> {
    private final EventDao eventDao;

    public EventProcessor(Receiver receiver, EventDao eventDao, Sherdog sherdog) {
        super(receiver, sherdog);
        this.eventDao = eventDao;
    }


    @Override
    protected void propagate(MmathEvent event) {
        event.getFights().forEach(f -> {
            receiver.process(new ProcessItem(f.getFighter1().getSherdogUrl(), ProcessType.FIGHTER));
            receiver.process(new ProcessItem(f.getFighter2().getSherdogUrl(), ProcessType.FIGHTER));
        });

        receiver.process(new ProcessItem(event.getOrganization().getSherdogUrl(), ProcessType.ORGANIZATION));
    }

    @Override
    protected void insertToDao(MmathEvent event) {
        eventDao.insert(event);
    }

    @Override
    protected void updateToDao(MmathEvent old, MmathEvent event) {
        event.setLastUpdate(LocalDate.now());
        eventDao.update(event);
    }

    @Override
    protected MmathEvent getFromSherdog(String url) throws IOException, ParseException {
        Event event = sherdog.getEvent(url);
        return new MmathEvent(event);
    }

    @Override
    protected LocalDate getLastUpdate(MmathEvent event) {
        return event.getLastUpdate();
    }

    @Override
    protected Optional<MmathEvent> getFromDao(String url) {
        return eventDao.getByUrl(url);
    }
}

