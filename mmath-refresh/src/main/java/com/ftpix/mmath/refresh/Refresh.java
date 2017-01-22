package com.ftpix.mmath.refresh;

import com.ftpix.calculator.client.CalculatorClient;
import com.ftpix.mmath.dao.FighterDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by gz on 25-Sep-16.
 */
public class Refresh {
    private Logger logger = LogManager.getLogger();
    private final RabbitTemplate fighterTemplate;
    private final FighterDao fighterDao;
    private final CalculatorClient calculatorClient;


    public Refresh(RabbitTemplate fighterTemplate, FighterDao fighterDao, CalculatorClient calculatorClient) {
        this.fighterTemplate = fighterTemplate;
        this.fighterDao = fighterDao;
        this.calculatorClient = calculatorClient;
    }


    public void process() {
        logger.info("Starting job");

        final LocalDate today = LocalDate.now();

        fighterDao.getAll().parallelStream()
                .forEach(f -> {
                    //refreshing the count for each fighter

                   /*  Optional.ofNullable(calculatorClient.betterThanCount(f.getId()).execute().body()).ifPresent(count ->{
                         f.setBetterThan(count);
                     });

                    Optional.ofNullable(calculatorClient.betterThanCount(f.getId()).execute().body()).ifPresent(count ->{
                        f.setWeakerThan(count);
                    });


                    f.setLastCountUpdate(today);

                    logger.info("Updating fighter {} with better than {} weaker than {}", f.getName(), f.getBetterThan(), f.getWeakerThan());
                    fighterDao.update(f);
                    */

                    if (DAYS.between(f.getLastUpdate(), today) > 5) {
                        logger.info("Sending {} for refresh", f.getName());
                        fighterTemplate.convertAndSend(f.getSherdogUrl());
                    }


                });

        logger.info("Job done");

    }

}
