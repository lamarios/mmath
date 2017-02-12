package com.ftpix.mmath.caching;

import com.google.gson.Gson;

import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.utils.GsonUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by gz on 12-Feb-17.
 * This will try to get from Redis then db if available.
 */
public class FighterCache {
    private final Gson gson = GsonUtils.getGson();
    private final JedisPool pool;
    private final FighterDao dao;
    private final RabbitTemplate fighterTemplate;
    private final Logger logger = LogManager.getLogger();

    public FighterCache(JedisPool pool, FighterDao fighterDao, RabbitTemplate fighterTemplate) {
        this.pool = pool;
        this.dao = fighterDao;
        this.fighterTemplate = fighterTemplate;
    }

    public Optional<MmathFighter> get(String id) {
        try (Jedis jedis = pool.getResource()) {
            Optional<MmathFighter> fighter = Optional.ofNullable(gson.fromJson(jedis.get(id), MmathFighter.class));


            if (!fighter.isPresent()) {
                logger.info("Fighter [{}] not in redis, getting from DB and putting in redis", id);
                fighter = dao.get(id);

                fighter.ifPresent(f -> jedis.set(id, gson.toJson(f)));
            }

            //Updating in case it needs to
            fighter.ifPresent(f -> {
                if (ChronoUnit.DAYS.between(f.getLastUpdate(), LocalDate.now()) > 3) {
                    logger.info("Fighter [{}] data too old, sending to MQ for a refresh", id);
                    fighterTemplate.convertAndSend(f.getSherdogUrl());
                }
            });



            return fighter;
        }
    }
}
