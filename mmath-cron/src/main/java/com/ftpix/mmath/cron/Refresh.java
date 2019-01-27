package com.ftpix.mmath.cron;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.Utils;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Organizations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by gz on 25-Sep-16.
 */
public class Refresh {
    private Logger logger = LogManager.getLogger();
    private final JmsTemplate jmsTemplate;
    private final MySQLDao dao;
    private final String fighterTopic;
    private final String eventTopic;
    private final String organizationTopic;

    public static int RATE = 1;

    public Refresh(JmsTemplate jmsTemplate, MySQLDao dao, String fighterTopic, String eventTopic, String organizationTopic) {
        this.jmsTemplate = jmsTemplate;
        this.dao = dao;
        this.fighterTopic = fighterTopic;
        this.eventTopic = eventTopic;
        this.organizationTopic = organizationTopic;
    }


    public void process() {
        logger.info("Starting job");

        final LocalDateTime today = LocalDateTime.now();


        logger.info("Deleting all the fights that have not happened yet");
        dao.getFightDAO().deleteAllNotHappenedFights();
        dao.getEventDAO().deleteNotHappenedEvents();

        jmsTemplate.convertAndSend(fighterTopic, "fighter/Alistair-Overeem-461");
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.UFC.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.BELLATOR.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.INVICTA_FC.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.ONE_FC.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.WSOF.url));

        dao.getFighterDAO().getAll().parallelStream()
                .forEach(f -> {
                    //refreshing the count for each fighter


                    if (DAYS.between(f.getLastUpdate(), today) >= RATE) {
                        logger.info("Sending {} for refresh", f.getName());
                        jmsTemplate.convertAndSend(fighterTopic, f.getSherdogUrl());
                    }


                });


        logger.info("Job done");

    }

}
