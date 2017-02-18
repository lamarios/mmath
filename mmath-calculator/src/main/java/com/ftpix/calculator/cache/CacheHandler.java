package com.ftpix.calculator.cache;

import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

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

    public void refresh() {
        logger.info("Refreshing cache...");


        fighters.putAll(dao.getAllAsMap());
        logger.info("Cache size: {}", fighters.size());

    }


    public Map<String, MmathFighter> getFighters() {
        return fighters;
    }

}
