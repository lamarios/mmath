package com.ftpix.mmath.cacheslave.stats.implementations.drawpercentage;

import com.ftpix.mmath.cacheslave.stats.StatsProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NCPercentageStats extends StatsProcessor {
    public NCPercentageStats(MySQLDao dao) {
        super(dao);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("NC_STAT");
        cat.setDescription("Highest number of no contest fights.");
        cat.setName("No fights");
        cat.setOrder(5);
        return cat;
    }

    @Override
    protected List<StatsEntry> generateEntries() {
        List<MmathFighter> fighters = dao.getFighterDAO().getAll().stream()
                .filter(f -> getTotalFights(f) >= 10)
                .collect(Collectors.toList());
        double maxNc = getMaxNC(fighters);
        return fighters.stream()
                .map(f -> {
                    StatsEntry stats = new StatsEntry();
                    stats.setFighter(f);
                    stats.setPercent((int) ((double) f.getNc() / maxNc * 100));
                    stats.setTextToShow(
                            f.getNc()
                                    + " ("
                                    + String.valueOf((int) getNCPercentage(f))
                                    + "%) NC of total "
                                    + (int) getTotalFights(f)
                                    + " fights");
                    return stats;
                })
                .sorted((se1, se2) -> Integer.compare(se2.getFighter().getNc(), se1.getFighter().getNc()))
                .collect(Collectors.toList());
    }

    private double getMaxNC(List<MmathFighter> fighters) {
        return (double) fighters.stream()
                .max(Comparator.comparingInt(MmathFighter::getNc))
                .map(MmathFighter::getNc)
                .get();
    }

    private double getNCPercentage(MmathFighter fighter) {
        return ((double) fighter.getNc() / getTotalFights(fighter))
                * 100;
    }

    private double getTotalFights(MmathFighter fighter) {
        return (double) fighter.getDraws() +
                (double) fighter.getWins() +
                (double) fighter.getNc() +
                (double) fighter.getLosses();
    }
}