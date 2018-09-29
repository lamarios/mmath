package com.ftpix.mmath.cron.stats.implementations;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HighestNcStats extends StatsProcessor {
    public HighestNcStats(MySQLDao dao) {
        super(dao);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("HIGH_NC");
        cat.setDescription("Highest number of no contest fights.");
        cat.setName("No contests");
        cat.setOrder(6);
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
                                    + " NC of total "
                                    + (int) getTotalFights(f)
                                    + " fights");
                    return stats;
                })
//                .sorted((se1, se2) -> Integer.compare(se2.getFighter().getNc(), se1.getFighter().getNc()))
                .sorted(Comparator.comparingInt((StatsEntry s) -> s.getFighter().getNc()).reversed().thenComparing((o1, o2) -> Double.compare(getNCPercentage(o2.getFighter()), getNCPercentage(o1.getFighter()))))
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
