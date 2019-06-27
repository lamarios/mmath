package com.ftpix.mmath.cron.stats.implementations;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;

import java.util.*;
import java.util.stream.Collectors;

public class GlassCannonStats extends StatsProcessor {
    public GlassCannonStats(MySQLDao dao) {
        super(dao);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory category = new StatsCategory();
        category.setName("Glass cannons");
        category.setId("GLASS_CANNON");
        category.setOrder(0);
        category.setDescription("Fighters with only (T)KO wins or losses");
        return category;
    }


    private int countFighterFights(MmathFighter f) {
        return f.getWins() + f.getLosses();
    }

    @Override
    protected List<StatsEntry> generateEntries() {
        List<MmathFighter> top100 = new ArrayList<>();

        BatchProcessor.forClass(MmathFight.class, 100)
                .withSupplier((batch, batchSize, offset) -> dao.getFightDAO().getBatch(offset, batchSize))
                .withProcessor(fights -> {

                    Set<String> fightersWithKO = new HashSet<>();
                    Set<String> fightersWithOthers = new HashSet<>();


                    fights.stream()
                            .filter(f -> f.getFightType() == FightType.PRO || f.getFightType() == FightType.PRO_EXHIBITION)
                            .filter(f -> f.getResult() != FightResult.NOT_HAPPENED)
                            .forEach(f -> {
                                boolean hasFighter1 = f.getFighter1() != null;
                                boolean hasFighter2 = f.getFighter2() != null;

                                if (f.getWinMethod().toLowerCase().contains("ko")) {
                                    if (hasFighter1) {
                                        logger.info("Adding {} to fightersWithKo", f.getFighter1().getSherdogUrl());
                                        fightersWithKO.add(f.getFighter1().getSherdogUrl());
                                    }

                                    if (hasFighter2) {
                                        logger.info("Adding {} to fightersWithKo", f.getFighter2().getSherdogUrl());
                                        fightersWithKO.add(f.getFighter2().getSherdogUrl());
                                    }

                                } else {
                                    if (hasFighter1) {
                                        logger.info("Adding {} to fighterWithOthers", f.getFighter1().getSherdogUrl());
                                        fightersWithOthers.add(f.getFighter1().getSherdogUrl());
                                    }

                                    if (hasFighter2) {
                                        logger.info("Adding {} to fighterWithOthers", f.getFighter2().getSherdogUrl());
                                        fightersWithOthers.add(f.getFighter2().getSherdogUrl());
                                    }
                                }
                            });

                    logger.info("{} fighters with KO,  {} with decisions", fightersWithKO.size(), fightersWithOthers.size());

                    fightersWithKO.removeIf(fightersWithOthers::contains);

                    logger.info("Found {} fighters with KO or TKO only", fightersWithKO.size());

                    top100.addAll(fightersWithKO.stream()
                            .map(f -> dao.getFighterDAO().getById(f))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
                    );

                    List<MmathFighter> newTop100 =
                            top100.stream()
                                    .sorted((f1, f2) -> Integer.compare(countFighterFights(f2), countFighterFights(f1)))
                                    .limit(100)
                                    .collect(Collectors.toList());


                    top100.clear();
                    top100.addAll(newTop100);

                }).start();


        long reference = countFighterFights(top100.get(0));


        return top100.stream()
                .map(f -> {

                    f.setFights(dao.getFightDAO().getByFighter(f.getSherdogUrl()));
                    return f;
                })
                .map(f -> {
                    StatsEntry e = new StatsEntry();
                    e.setFighter(f);
                    e.setTextToShow(countFighterFights(f) + " fights");
                    e.setDetails(f.getName() + "has " + f.getFights().size() + " fights and they all finished by a KO or a TKO, " + f.getWins() + " wins for " + f.getLosses() + " losses");

                    double percent = ((double) countFighterFights(f) / (double) reference) * 100;
                    e.setPercent((int) Math.ceil(percent));
                    return e;
                })
                .collect(Collectors.toList());
    }
}
