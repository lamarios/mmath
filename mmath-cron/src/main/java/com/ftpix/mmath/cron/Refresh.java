package com.ftpix.mmath.cron;

import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.Utils;
import com.ftpix.sherdogparser.models.Organizations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by gz on 25-Sep-16.
 */
@Component
public class Refresh {
    private Logger logger = LogManager.getLogger();


    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private String fighterTopic;
    @Autowired
    private String eventTopic;
    @Autowired
    private String organizationTopic;


    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private FighterDAO fighterDAO;

    @Autowired
    private StatsEntryDAO statsEntryDAO;

    @Autowired
    private StatsCategoryDAO statsCategoryDAO;

    public static int RATE = 1;


    @Scheduled(cron = "0 6 11 * * ?")
    public void cleanDB(){
        fightDAO.cleanFightersRecord();
    }

    @Scheduled(cron = "0 0 0 ? * TUE")
    public void process() {
        logger.info("Starting job");

        final LocalDateTime today = LocalDateTime.now();


        logger.info("Deleting all the fights that have not happened yet");
        fightDAO.deleteAllNotHappenedFights();
        eventDAO.deleteNotHappenedEvents();

        jmsTemplate.convertAndSend(fighterTopic, "fighter/Alistair-Overeem-461");
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.UFC.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.BELLATOR.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.INVICTA_FC.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.ONE_FC.url));
        jmsTemplate.convertAndSend(organizationTopic, Utils.cleanUrl(Organizations.WSOF.url));


        BatchProcessor.forClass(MmathFighter.class, 100)
                .withSupplier((batch, batchSize, offset) -> fighterDAO.getBatch(offset, batchSize))
                .withProcessor(fighters -> fighters
                        .parallelStream()
                        .forEach(f -> {
                            //refreshing the count for each fighter
                            if (DAYS.between(f.getLastUpdate(), today) >= RATE) {
//                                logger.info("Sending {} for refresh", f.getName());
                                jmsTemplate.convertAndSend(fighterTopic, f.getSherdogUrl());
                            }


                        })).start();

        logger.info("Job done");

    }

}
