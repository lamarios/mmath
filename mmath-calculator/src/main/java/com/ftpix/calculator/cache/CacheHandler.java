package com.ftpix.calculator.cache;

import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gz on 24-Sep-16.
 */
public class CacheHandler {
    private Map<String, MmathFighter> fighters;
    private FighterDao dao;
    private final Logger logger = LogManager.getLogger();

    public CacheHandler(FighterDao dao, Map<String, MmathFighter> fighters) {
        this.dao = dao;
        this.fighters = fighters;
    }

    public void refreshCache() {
        logger.info("Refreshing cache...");

        Set<String> keys = fighters.keySet();

        fighters.putAll(dao.getAllAsMap());
        logger.info("Cache size: {}", fighters.size());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refreshCache();
            }
            //}, 1000 * 60 * 2);
        }, 1000 * 60 * 60 * 2);
    }


    public Map<String, MmathFighter> getFighters() {
        return fighters;
    }

}
