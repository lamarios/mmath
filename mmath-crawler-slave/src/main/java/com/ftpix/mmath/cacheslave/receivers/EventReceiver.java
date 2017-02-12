package com.ftpix.mmath.cacheslave.receivers;

import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

import redis.clients.jedis.JedisPool;

/**
 * Created by gz on 16-Sep-16.
 */
public class EventReceiver extends Receiver<MmathEvent> {


    public EventReceiver(RabbitTemplate fighterTemplate, RabbitTemplate orgTemplate, RabbitTemplate eventTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog, JedisPool jedisPool) {
        super(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog, jedisPool);
    }

    /*public void receiveMessage(String message) {
        logger.info("Event receiver:{}", message);

        try {

            Document query = new Document("sherdogUrl", message);

            if (eventCollection.count(query) == 0) {
                MmathEvent event = new MmathEvent(sherdog.getEvent(message));

                eventCollection.insertOne(EventParser.serialize(event));

                event.getFights()

                orgTemplate.convertAndSend(event.getOrganization().getSherdogUrl());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void propagate(MmathEvent event) {
        event.getFights().forEach(f -> {
            fighterTemplate.convertAndSend(f.getFighter2().getSherdogUrl());
            fighterTemplate.convertAndSend(f.getFighter1().getSherdogUrl());

        });

        orgTemplate.convertAndSend(event.getOrganization().getSherdogUrl());
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
        return  new MmathEvent(event);
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

