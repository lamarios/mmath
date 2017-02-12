package com.ftpix.mmath.cacheslave.receivers;

import com.google.gson.Gson;

import com.ftpix.mmath.cacheslave.ShutdownTimer;
import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.mmath.model.MmathModel;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.utils.GsonUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by gz on 16-Sep-16.
 */
public abstract class Receiver<T extends MmathModel> {

    protected Logger logger = LogManager.getLogger();
    protected final RabbitTemplate fighterTemplate, orgTemplate, eventTemplate;
    protected final FighterDao fighterDao;
    protected final EventDao eventDao;
    protected final OrganizationDao orgDao;
    protected final Sherdog sherdog;
    protected JedisPool jedisPool;
    private Gson gson = GsonUtils.getGson();

    public Receiver(RabbitTemplate fighterTemplate, RabbitTemplate orgTemplate, RabbitTemplate eventTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog, JedisPool jedisPool) {
        this.fighterTemplate = fighterTemplate;
        this.orgTemplate = orgTemplate;
        this.eventTemplate = eventTemplate;
        this.fighterDao = fighterDao;
        this.eventDao = eventDao;
        this.orgDao = orgDao;
        this.sherdog = sherdog;
        this.jedisPool = jedisPool;
    }

    public void receiveMessageAsBytes(byte[] message) {
        receiveMessageAsBytes(new String(message));
    }

    public void receiveMessageAsBytes(String message) {
        ShutdownTimer.start();
        process(message);
    }

    //protected abstract void receiveMessage(String s);


    private void process(String message) {

        logger.info("{} received:{}", this.getClass().getName(), message);

        try {


            Optional<T> opt = getFromDao(message);

            Optional<T> toParse = Optional.empty();

            if (opt.isPresent()) {
                logger.info("[{}] already exists...", message);
                LocalDate now = LocalDate.now();
                T optResult = opt.get();


                long daysbetween = ChronoUnit.DAYS.between(getLastUpdate(optResult), now);

                if (daysbetween >= 3) {
                    logger.info("[{}] Info is too old, need to update", message);
                    T updated = getFromSherdog(message);


                    updateToDao(optResult, updated);

                    toParse = Optional.ofNullable(updated);

                }

            } else {
                logger.info("[{}] doesn't exist, need to get and insert", message);

                T obj = getFromSherdog(message);
                insertToDao(obj);
                toParse = Optional.ofNullable(obj);
            }


            toParse.ifPresent(obj -> {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.set(obj.getId(), gson.toJson(obj));
                }
                propagate(obj);

            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract void propagate(T obj);

    protected abstract void insertToDao(T obj);

    protected abstract void updateToDao(T old, T fromSherdog);

    protected abstract T getFromSherdog(String url) throws IOException, ParseException;

    protected abstract LocalDate getLastUpdate(T obj);

    protected abstract Optional<T> getFromDao(String url);

}
