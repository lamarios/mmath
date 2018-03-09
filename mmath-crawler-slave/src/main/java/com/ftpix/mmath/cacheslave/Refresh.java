package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.dao.MySQLDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created by gz on 25-Sep-16.
 */
public class Refresh {
    private Logger logger = LogManager.getLogger();
    private final MySQLDao dao;
    private final Receiver receiver;

    public static int RATE = 1;

    public Refresh(Receiver receiver, MySQLDao dao) {
        this.dao = dao;
        this.receiver = receiver;
    }


    public void process() throws SQLException {
        logger.info("Starting job");

        final LocalDateTime today = LocalDateTime.now();
/*
        receiver.process(new ProcessItem("http://www.sherdog.com/fighter/Alistair-Overeem-461", ProcessType.FIGHTER));
        receiver.process(new ProcessItem(Organizations.UFC.url, ProcessType.ORGANIZATION));
        receiver.process(new ProcessItem(Organizations.BELLATOR.url, ProcessType.ORGANIZATION));
        receiver.process(new ProcessItem(Organizations.INVICTA_FC.url, ProcessType.ORGANIZATION));
        receiver.process(new ProcessItem(Organizations.ONE_FC.url, ProcessType.ORGANIZATION));
        receiver.process(new ProcessItem(Organizations.WSOF.url, ProcessType.ORGANIZATION));

        dao.getFighterDAO().getAll().parallelStream()
                .forEach(f -> {
                    //refreshing the count for each fighter


                    if (DAYS.between(f.getLastUpdate(), today) >= RATE) {
                        logger.info("Sending {} for refresh", f.getName());
                        receiver.process(new ProcessItem(f.getSherdogUrl(), ProcessType.FIGHTER));
                    }


                });

        logger.info("Job done");

    */
    }

}
