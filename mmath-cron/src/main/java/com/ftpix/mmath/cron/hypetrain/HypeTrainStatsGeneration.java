package com.ftpix.mmath.cron.hypetrain;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.HypeTrainStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class HypeTrainStatsGeneration {

    private final MySQLDao dao;
    protected Logger logger = LogManager.getLogger();

    public HypeTrainStatsGeneration(MySQLDao dao) {
        this.dao = dao;
    }


    public void process() {

        LocalDate date = LocalDate.now();

        String statsDate = date.getYear() + "-" + String.format("%02d", date.getMonthValue());


        dao.getHypeTrainDAO().getAllCounts(Integer.MAX_VALUE)
                .stream()
                .map(s -> {
                    HypeTrainStats stats = new HypeTrainStats();
                    stats.setMonth(statsDate);
                    stats.setFighter(s.getFighter());
                    stats.setCount(s.getCount());


                    logger.info("Inserting stats for fighter [{}] month:[{}] count [{}]", stats.getFighter(), stats.getMonth(), stats.getCount());
                    return stats;

                })
                .forEach(dao.getHypeTrainDAO()::insertStats);
    }

}
