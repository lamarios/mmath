package com.ftpix.mmath.cacheslave.stats.implementations;

import com.ftpix.mmath.cacheslave.stats.StatsProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.sherdogparser.models.FightResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GlassCannonStats extends StatsProcessor {
    public GlassCannonStats(MySQLDao dao) {
        super(dao);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory category = new StatsCategory();
        category.setName("Glass Cannons");
        category.setId("GLASS_CANNON");
        category.setDescription("Fighters with only (T)KO wins or losses");
        return category;
    }


    private int countFighterFights(MmathFighter f) {
        return f.getWins() + f.getLosses();
    }

    @Override
    protected List<StatsEntry> generateEntries() {
        Set<String> fightersWithKO = new HashSet<>();
        Set<String> fightersWithOthers = new HashSet<>();

        dao.getFightDAO().getAll()
                .stream()
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

        List<MmathFighter> top100 = fightersWithKO.stream()
                .map(f -> dao.getFighterDAO().getById(f))
                .sorted((f1, f2) -> {
                    return Integer.compare(countFighterFights(f2), countFighterFights(f1));
                })
                .limit(100)
                .collect(Collectors.toList());

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
