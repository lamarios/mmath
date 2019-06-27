package com.ftpix.mmath.cron.stats.implementations;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;

import java.util.ArrayList;
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
        cat.setName("Warrior spirit");
        cat.setDescription("Fighters with the most fights");
        cat.setOrder(2);
        return cat;
    }

    private int countFighterFights(MmathFighter f) {
        return f.getWins() + f.getLosses() + f.getDraws() + f.getNc();
    }

    @Override
    protected List<StatsEntry> generateEntries() {

        List<MmathFighter> top100 = new ArrayList<>();

        BatchProcessor.forClass(MmathFighter.class, 100)
                .withSupplier((batch, batchSize, offset) -> dao.getFighterDAO().getBatch(offset, batchSize))
                .withProcessor(fighters -> {
                    top100.addAll(fighters);
                    List<MmathFighter> newTop100 = top100.stream()
                            .sorted((f1, f2) -> Integer.compare(countFighterFights(f2), countFighterFights(f1)))
                            .limit(100)
                            .collect(Collectors.toList());


                    top100.clear();
                    top100.addAll(newTop100);
                }).start();


        int reference = countFighterFights(top100.get(0));

        //now creating our stats entries

        return top100.stream()
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
