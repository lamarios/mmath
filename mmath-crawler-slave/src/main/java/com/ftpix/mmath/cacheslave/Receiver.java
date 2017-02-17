package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.processors.EventProcessor;
import com.ftpix.mmath.cacheslave.processors.FighterProcessor;
import com.ftpix.mmath.cacheslave.processors.OrganizationProcessor;
import com.ftpix.mmath.cacheslave.processors.Processor;
import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.sherdogparser.Sherdog;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.JedisPool;

/**
 * Created by gz on 17-Feb-17.
 */
public class Receiver {
    private final static int CORE_POOL_SIZE = 1, MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 15, THREAD_TIMEOUT = 60;
    private final ThreadPoolExecutor fightPool, eventPool, orgPool;
    private final Processor fightProcessor, eventProcessor, orgProcessor;


    public Receiver(FighterDao fighterDao, EventDao eventDao, OrganizationDao organizationDao, JedisPool jedisPool, Sherdog sherdog) {
        this.fightPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, THREAD_TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        this.eventPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, THREAD_TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        this.orgPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, THREAD_TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        this.fightProcessor = new FighterProcessor(this, fighterDao, sherdog, jedisPool);
        this.eventProcessor = new EventProcessor(this, eventDao, sherdog, jedisPool);
        this.orgProcessor = new OrganizationProcessor(this, organizationDao, sherdog, jedisPool);
    }

    public void process(ProcessItem item) {
        ThreadPoolExecutor pool = null;
        Processor processor = null;
        switch (item.getType()) {
            case FIGHTER:
                pool = fightPool;
                processor = fightProcessor;
                break;
            case EVENT:
                pool = eventPool;
                processor = eventProcessor;
                break;
            case ORGANIZATION:
                pool = orgPool;
                processor = orgProcessor;
                break;
        }

        Processor finalProcessor = processor;

        Optional.ofNullable(pool).ifPresent(p -> {
            p.execute(() -> {
                finalProcessor.process(item);
            });
        });
    }
}
