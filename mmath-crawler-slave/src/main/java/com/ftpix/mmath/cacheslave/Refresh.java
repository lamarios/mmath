package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.utils.DateUtils;
import com.j256.ormlite.dao.Dao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by gz on 25-Sep-16.
 */
public class Refresh {
    private Logger logger = LogManager.getLogger();
    private final Dao<MmathFighter, String> fighterDao;
    private final Receiver receiver;

    public static int RATE = 3;

    public Refresh(Receiver receiver, Dao<MmathFighter, String> fighterDao) {
        this.fighterDao = fighterDao;
        this.receiver = receiver;
    }


    public void process() throws SQLException {
        logger.info("Starting job");

        final LocalDateTime today = LocalDateTime.now();

        receiver.process(new ProcessItem("http://www.sherdog.com/fighter/Alistair-Overeem-461", ProcessType.FIGHTER));

        fighterDao.queryForAll().parallelStream()
                .forEach(f -> {
                    //refreshing the count for each fighter


                    if (DAYS.between(DateUtils.toLocalDateTime(f.getLastUpdate()), today) >= RATE) {
                        logger.info("Sending {} for refresh", f.getName());
                        receiver.process(new ProcessItem(f.getSherdogUrl(), ProcessType.FIGHTER));
                    }


                });

        logger.info("Job done");

    }

}
