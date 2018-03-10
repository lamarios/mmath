package com.ftpix.mmath.cacheslave.stats.implementations;

import com.ftpix.mmath.cacheslave.stats.StatsProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;

import java.util.List;
import java.util.stream.Collectors;

public class MostFightStats extends StatsProcessor {

    public MostFightStats(MySQLDao dao) {
        super(dao);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("MOST_FIGHTS");
        cat.setName("Warrior Spirit");
        cat.setDescription("Fighters with the most fights");
        return cat;
    }

    private int countFighterFights(MmathFighter f) {
        return f.getWins() + f.getLosses() + f.getDraws() + f.getNc();
    }

    @Override
    protected List<StatsEntry> generateEntries() {


        List<MmathFighter> fighters = dao.getFighterDAO().getAll().stream()
                .sorted((f1, f2) -> {
                    return Integer.compare(countFighterFights(f2), countFighterFights(f1));
                })
                .limit(100)
                .collect(Collectors.toList());

        int reference = countFighterFights(fighters.get(0));

        //now creating our stats entries

        return fighters.stream()
                .map(f -> {
                    StatsEntry e = new StatsEntry();
                    e.setFighter(f);
                    e.setTextToShow(countFighterFights(f) + " fights");
                    e.setDetails(f.getWins() + " wins, " + f.getLosses() + " losses, " + f.getDraws() + " draws, " + f.getNc() + " no contests");
                    double percent = ((double) countFighterFights(f) / (double) reference) * 100;
                    e.setPercent((int) Math.ceil(percent));
                    return e;
                })
                .collect(Collectors.toList());
    }
}
