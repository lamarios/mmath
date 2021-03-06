package com.ftpix.mmath.cron.stats.implementations;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
public class LongestCareer extends StatsProcessor {

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private FighterDAO fighterDAO;

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setDescription("Longest amount of time between first and last fight");
        cat.setId("LONGEST_CAREER");
        cat.setName("Old dogs");
        cat.setOrder(1);
        return cat;
    }

    @Override
    protected List<StatsEntry> generateEntries() {

        Map<String, Long> top100 = new LinkedHashMap<>();

        BatchProcessor.forClass(MmathFighter.class, 100)
                .withSupplier((batch, batchSize, offset) -> fighterDAO.getBatch(offset, batchSize))
                .withProcessor(fighters -> {
                    Map<String, ZonedDateTime> earliestFight = new HashMap<>();
                    Map<String, ZonedDateTime> mostRecentFight = new HashMap<>();

                    fighters.stream()
                            .filter(f -> f.getSherdogUrl() != null && f.getSherdogUrl().trim().length() > 0)
                            .forEach(fighter -> {
                                List<MmathFight> fights = fightDAO.getByFighter(fighter.getSherdogUrl())
                                        .stream()
                                        .filter(f -> f.getFightType() == FightType.PRO || f.getFightType() == FightType.PRO_EXHIBITION)
                                        .filter(f -> f.getResult() != FightResult.NOT_HAPPENED)
                                        .filter(f -> f.getEvent() != null && f.getEvent().getDate() != null)
                                        .collect(Collectors.toList());
                                if (!fights.isEmpty()) {
                                    long careerLength = Math.abs(ChronoUnit.DAYS.between(fights.get(0).getEvent().getDate(), fights.get(fights.size() - 1).getEvent().getDate()));

                                    top100.put(fighter.getSherdogUrl(), careerLength);
                                }


                            });


                    Map<String, Long> newTop100 = new LinkedHashMap<>();

                    top100.keySet().stream()
                            .sorted((f1, f2) -> Long.compare(top100.get(f2), top100.get(f1)))
                            .limit(100)
                            .forEach(key -> newTop100.put(key, top100.get(key)));


                    top100.clear();
                    top100.putAll(newTop100);

                })
                .start();


        long reference = top100.entrySet().iterator().next().getValue();

        return top100.keySet().stream()
                .map(f -> {
                    StatsEntry e = new StatsEntry();
                    MmathFighter fighter = fighterDAO.getById(f);

                    e.setFighter(fighter);

                    long days = top100.get(f);
                    int percent = (int) Math.ceil(((double) days / (double) reference) * 100);
                    e.setPercent(percent);

                    String text = (days / 365) + " years and " + (days % 365) + " days";
                    e.setTextToShow(text);

                    int fights = 0;
                    try {
                        fights = fighter.getWins() + fighter.getLosses() + fighter.getDraws() + fighter.getNc();
                    } catch (Exception exception) {
                        return null;
                    }
                    e.setDetails(
                            fighter.getName() + " had " + fights + " fights in the span of " + text
                    );

                    return e;

                })
                .filter(f -> f != null)
                .collect(Collectors.toList());
    }


    /*
    @Override
    protected List<StatsEntry> generateEntries() {
        Map<String, Long> fightersCareerLength = new HashMap<>();

        List<MmathFighter> top100 = dao.getFighterDAO().getAll()
                .parallelStream()
//                .filter(f -> f.getName().equalsIgnoreCase("Alistair Overeem"))
                .filter(f -> f.getWins()+f.getLosses()+f.getNc()+f.getDraws() > 1)
                .map(f -> {
                    //getting fights
                    logger.info("Getting fights for {}", f.getName());

                    List<MmathFight> fights = dao.getFightDAO().getByFighter(f.getSherdogUrl())
                            .stream()
                            .filter(fight -> fight.getResult() != FightResult.NOT_HAPPENED)
                            .map(fight -> {
                                MmathEvent event = dao.getEventDAO().getById(fight.getEvent().getSherdogUrl());
                                fight.setDate(event.getDate());
                                return fight;
                            })
                            .filter(fight -> fight.getDate() != null)
                            .collect(Collectors.toList());

                    f.setFights(fights);

                    logger.info("Calculating career length for {}", f.getName());
                    //calculating career length
                    if (f.getFights().size() > 1) {
                        MmathFight firstFight = f.getFights().get(0);

                        MmathFight lastFight = f.getFights().get(f.getFights().size() - 1);

                        long careerDays = Math.abs(ChronoUnit.DAYS.between(firstFight.getDate(), lastFight.getDate()));
                        fightersCareerLength.put(f.getSherdogUrl(), careerDays);
                        logger.info("{} has a career of {} days", f.getName(), careerDays);
                    } else {
                        fightersCareerLength.put(f.getSherdogUrl(), 0L);
                        logger.info("{} has only {} fights, skipping him", f.getName(), f.getFights().size());
                    }

                    i++;
                    System.out.println(i);
                    return f;
                })
                .sorted((f1, f2) -> {
                    return Long.compare(fightersCareerLength.get(f2.getSherdogUrl()), fightersCareerLength.get(f1.getSherdogUrl()));

                })
                .limit(100)
                .collect(Collectors.toList());

        //now doing the stats calculations
        long reference = fightersCareerLength.get(top100.get(0).getSherdogUrl());

        return top100.stream().map(f -> {
            StatsEntry e = new StatsEntry();
            e.setFigher(f);
            long days = fightersCareerLength.get(f.getSherdogUrl());
            //
            String text = days / 365 + " years and " + days % 365 + " days";
            e.setTextToShow(text);
            e.setDetails(f.getName() + " had " + f.getFights().size() + " fights within " + text);


            //calculating percent

            double percent = ((double) days / (double) reference) * 100;
            e.setPercent((int) Math.ceil(percent));

            return e;
        }).collect(Collectors.toList());
    }
    */
}
