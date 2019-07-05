package com.ftpix.mmath.cron.stats.implementations.winpercentage;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public abstract class WinPercentageStats extends StatsProcessor {

    protected WinPercentageStats() {
    }

    public enum Condition {
        KO( "%d out of %d wins  won by KO/TKO", "", MmathFighter::getWinKo),
        SUBMISSION( "%d out of %d wins won by submission", "", MmathFighter::getWinSub),
        DECISION( "%d out of %d wins won by decision", "", MmathFighter::getWinDec),
        ;

        Condition(String shortDescription, String longDescription, Function<MmathFighter, Integer> getWinsOfType) {
            this.shortDescription = shortDescription;
            this.longDescription = longDescription;
            this.getWinsOfType = getWinsOfType;
        }

        private Predicate<MmathFight> predicate;
        private String shortDescription, longDescription;
        private Function<MmathFighter, Integer> getWinsOfType;
    }


    protected Condition condition;

    @Autowired
    private FighterDAO fighterDAO;

    @Override
    protected abstract StatsCategory getStatsCategory();

    @Override
    protected List<StatsEntry> generateEntries() {
        List<MmathFighter> top100 = new ArrayList<>();

        BatchProcessor.forClass(MmathFighter.class, 100)
                .withSupplier((batch, batchSize, offset) -> fighterDAO.getBatch(offset, batchSize))
                .withProcessor(fighters -> {


                    top100.addAll(fighters);


                    List<MmathFighter> newTop100 = top100.stream()
                            .sorted((f1, f2) -> {

                                int total1 = condition.getWinsOfType.apply(f1);
                                int total2 = condition.getWinsOfType.apply(f2);


                                if (total1 == total2) {
                                    // if fighters have the same, we put first the one with the least fights as the percentage is higher
                                    return Integer.compare(f1.getWins(), f2.getWins());
                                } else {
                                    return Integer.compare(total2, total1); // more of condition first first
                                }


                            })
                            .limit(100)
                            .collect(Collectors.toList());


                    top100.clear();
                    top100.addAll(newTop100);

                }).start();


        if (top100.size() > 0) {
            int reference = condition.getWinsOfType.apply(top100.get(0));


            return top100.stream()
                    .map(f -> {
                        StatsEntry entry = new StatsEntry();

                        entry.setFighter(f);
                        entry.setTextToShow(String.format(condition.shortDescription, condition.getWinsOfType.apply(f), f.getWins()));


                        double percent = ((double) condition.getWinsOfType.apply(f) / (double) reference) * 100;
                        entry.setPercent((int) percent);

                        return entry;
                    })
                    .collect(Collectors.toList());
        }


        return new ArrayList<>();
    }


}
