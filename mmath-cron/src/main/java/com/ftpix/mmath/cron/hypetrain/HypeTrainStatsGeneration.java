package com.ftpix.mmath.cron.hypetrain;

import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.HypeTrainDAO;
import com.ftpix.mmath.model.AggregatedHypeTrain;
import com.ftpix.mmath.model.HypeTrainStats;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class HypeTrainStatsGeneration {
    protected Log logger = LogFactory.getLog(this.getClass());


    @Autowired
    private HypeTrainDAO hypeTrainDAO;


    @Scheduled(cron = "00 0 * * * ?")
    public void process() {

        LocalDate date = LocalDate.now();

        String statsDate = date.getYear() + "-" + String.format("%02d", date.getMonthValue());

        BatchProcessor.forClass(AggregatedHypeTrain.class, 100)
                .withSupplier((batch, batchSize, offset) -> hypeTrainDAO.getAllCounts(offset, batchSize))
                .withProcessor(trains -> trains
                        .stream()
                        .map(s -> {
                            HypeTrainStats stats = new HypeTrainStats();
                            stats.setMonth(statsDate);
                            stats.setFighter(s.getFighter());
                            stats.setCount(s.getCount());


                            logger.info("Inserting stats for fighter ["+stats.getFighter()+"] month:["+stats.getMonth()+"] count ["+stats.getCount()+"]");
                            return stats;

                        })
                        .forEach(hypeTrainDAO::insertStats)
                ).start();
    }

}
