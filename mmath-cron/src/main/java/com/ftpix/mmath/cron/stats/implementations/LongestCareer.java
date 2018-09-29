package com.ftpix.mmath.cron.stats.implementations;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.sherdogparser.models.FightResult;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LongestCareer extends StatsProcessor {
    private int i = 0;

    public LongestCareer(MySQLDao dao) {
        super(dao);
    }

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
        Map<String, ZonedDateTime> earliestFight = new HashMap<>();
        Map<String, ZonedDateTime> mostRecentFight = new HashMap<>();
        Map<String, MmathEvent> eventCache = new HashMap<>();

        dao.getFightDAO().getAll()
                .stream()
                .filter(f -> f.getResult() != FightResult.NOT_HAPPENED)
                .forEach(f -> {
                    ZonedDateTime date;
                    if (eventCache.containsKey(f.getEvent().getSherdogUrl())) {
                        date = eventCache.get(f.getEvent().getSherdogUrl()).getDate();
                    } else {
                        MmathEvent event = dao.getEventDAO().getById(f.getEvent().getSherdogUrl());
                        date = event.getDate();
                        eventCache.put(event.getSherdogUrl(), event);
                    }

                    Consumer<String> processFighter = fighter -> {
                        if (earliestFight.containsKey(fighter)) {
                            if (date.isBefore(earliestFight.get(fighter))) {
                                earliestFight.put(fighter, date);
                            }
                        } else {
                            earliestFight.put(fighter, date);
                        }


                        if (mostRecentFight.containsKey(fighter)) {
                            if (date.isAfter(mostRecentFight.get(fighter))) {
                                mostRecentFight.put(fighter, date);
                            }
                        } else {
                            mostRecentFight.put(fighter, date);
                        }
                    };

                    Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl)
                            .filter(s -> s.length() > 0)
                            .ifPresent(processFighter);

                    Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl)
                            .filter(s -> s.length() > 0)
                            .ifPresent(processFighter);
                });

        Map<String, Long> careerLengths = new HashMap<>();

        earliestFight.forEach((f, earliest) -> {
            Optional.ofNullable(mostRecentFight.get(f)).ifPresent(latest -> {
                careerLengths.put(f, Math.abs(ChronoUnit.DAYS.between(earliest, latest)));
            });
        });

        List<String> top100 = careerLengths.keySet().stream()
                .sorted((f1, f2) -> {
                    return Long.compare(careerLengths.get(f2), careerLengths.get(f1));
                })
                .limit(100)
                .collect(Collectors.toList());

        long reference = careerLengths.get(top100.get(0));

        return top100.stream()
                .map(f -> {
                    StatsEntry e = new StatsEntry();
                    MmathFighter fighter = dao.getFighterDAO().getById(f);

                    e.setFighter(fighter);

                    long days = careerLengths.get(f);
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
