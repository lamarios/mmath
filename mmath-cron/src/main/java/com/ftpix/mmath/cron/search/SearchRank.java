package com.ftpix.mmath.cron.search;

import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.EventDAO;
import com.ftpix.mmath.dao.mysql.FightDAO;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class SearchRank {
    protected Logger logger = LogManager.getLogger();

    @Autowired
    private FighterDAO fighterDAO;

    @Autowired
    private FightDAO fightDAO;

    @Autowired
    private EventDAO eventDAO;

    public void process() {
        BatchProcessor.forClass(MmathFighter.class, 100)
                .withSupplier((batch1, batchSize, offset) -> fighterDAO.getBatch(offset, batchSize))
                .withProcessor(fighters -> fighters.parallelStream().forEach(this::updateFighterRank))
                .start();
    }


    private int organizationScore(String orgSherdogUrl) {
        if (orgSherdogUrl == null)
            return 90000;
        switch (orgSherdogUrl) {
            case "organizations/Ultimate-Fighting-Championship-UFC-2":
                return 10000;
            case "organizations/Rizin-Fighting-Federation-10333":
            case "organizations/Bellator-MMA-1960":
            case "organizations/Professional-Fighters-League-12241":
                return 20000;
            case "organizations/Invicta-Fighting-Championships-4469":
            case "organizations/One-Championship-3877":
            case "organizations/World-Series-of-Fighting-5449":
                return 30000;
            case "organizations/Strikeforce-716":
            case "organizations/Dream-1357":
            case "organizations/Pride-Fighting-Championships-3":
                return 40000;
            default:
                return 90000;
        }
    }

    private void updateFighterRank(MmathFighter fighter) {
        try {
            logger.info("Updating search rank for {}", fighter.getName());


            List<MmathFight> fights = fightDAO.getByFighter(fighter.getSherdogUrl())
                    .stream()
                    .sorted(Comparator.comparing(MmathFight::getDate).reversed())
                    .collect(Collectors.toList());

            // finding the most popular organization the guy fought for
            Optional<String> bestOrg = fights.stream()
                    .map(f -> Optional.ofNullable(f.getEvent())
                            .map(e -> eventDAO.getById(e.getSherdogUrl()))
                            .map(e -> e.getOrganization().getSherdogUrl())
                            .orElse("")
                    ).min(Comparator.comparingInt(this::organizationScore));


            //now we need to find how long ago in years is the last fight. active fighters should come up first
            // and how many fights in this organization they have. long time active fighters should come first too

            AtomicInteger count = new AtomicInteger(0);

            long yearsSinceLastFight = fights.stream()
                    .filter(f -> Optional.ofNullable(f.getEvent())
                            .map(e -> eventDAO.getById(e.getSherdogUrl()))
                            .map(e -> e.getOrganization().getSherdogUrl())
                            .orElse("").equalsIgnoreCase(bestOrg.get()))
                    .mapToLong(f -> { // counting years since last fight
                        count.incrementAndGet();
                        return Math.abs(ChronoUnit.YEARS.between(f.getDate(), ZonedDateTime.now())) * 1000;
                    })
                    .min()
                    .orElse(9999);


            int searchRank = organizationScore(bestOrg.get()) + (int) yearsSinceLastFight + (999 - count.get());
            fighter.setSearchRank(searchRank);

            fighterDAO.update(fighter);

            logger.info("Fighter  search rank: {}, bestOrg [{}], years since last fight [{}], fight count [{}]", fighter.getSearchRank(), bestOrg.get(), yearsSinceLastFight, count.get());
        } catch (Exception e) {
            e.printStackTrace();
            fighter.setSearchRank(99999);
            fighterDAO.update(fighter);
        }
    }

}
