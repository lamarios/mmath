package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.dao.FighterDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by gz on 25-Sep-16.
 */
public class Refresh {
    private Logger logger = LogManager.getLogger();
    private final FighterDao fighterDao;
    private final Receiver receiver;

    public static int RATE = 3;

    public Refresh(Receiver receiver, FighterDao fighterDao) {
        this.fighterDao = fighterDao;
        this.receiver = receiver;
    }


    public void process() {
        logger.info("Starting job");

        final LocalDate today = LocalDate.now();

        receiver.process(new ProcessItem("http://www.sherdog.com/fighter/Alistair-Overeem-461", ProcessType.FIGHTER));

        fighterDao.getAll().parallelStream()
                .forEach(f -> {
                    //refreshing the count for each fighter


                    if (DAYS.between(f.getLastUpdate(), today) >= RATE) {
                        logger.info("Sending {} for refresh", f.getName());
                        receiver.process(new ProcessItem(f.getSherdogUrl(), ProcessType.FIGHTER));
                    }


                });

        logger.info("Job done");

    }

}
