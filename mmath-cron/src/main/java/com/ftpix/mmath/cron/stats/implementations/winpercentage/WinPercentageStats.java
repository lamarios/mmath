package com.ftpix.mmath.cron.stats.implementations.winpercentage;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class WinPercentageStats extends StatsProcessor {

    public enum Condition {
        KO(f -> f.getWinMethod() != null && f.getWinMethod().contains("KO"), "%d (%d%%) wins by KO/TKO", ""),
        SUBMISSION(f -> f.getWinMethod() != null && f.getWinMethod().toLowerCase().contains("submission"), "%d (%d%%) wins by submission", ""),
        DECISION(f -> f.getWinMethod() != null && f.getWinMethod().toLowerCase().contains("decision"), "%d (%d%%) wins by decision", ""),
        ;

        Condition(Predicate<MmathFight> condition, String shortDescription, String longDescription) {
            this.predicate = condition;
            this.shortDescription = shortDescription;
            this.longDescription = longDescription;
        }

        private Predicate<MmathFight> predicate;
        private String shortDescription, longDescription;
    }


    private final Condition condition;

    protected WinPercentageStats(MySQLDao dao, Condition condition) {
        super(dao);
        this.condition = condition;
    }

    @Override
    protected abstract StatsCategory getStatsCategory();

    @Override
    protected List<StatsEntry> generateEntries() {
        Map<String, FighterStat> stats = new HashMap<>();

        dao.getFightDAO().getAll()
                .stream()
                .filter(f -> f.getFightType() == FightType.PRO || f.getFightType() == FightType.PRO_EXHIBITION)
                .forEach(f -> {
                    Optional<String> fighter1 = Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl);
                    Optional<String> fighter2 = Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl);


                    if (fighter1.isPresent() && !stats.containsKey(fighter1.get())) {
                        stats.put(fighter1.get(), new FighterStat(fighter1.get()));
                    }
                    if (fighter2.isPresent() && !stats.containsKey(fighter2.get())) {
                        stats.put(fighter2.get(), new FighterStat(fighter2.get()));
                    }


                    fighter1.ifPresent(fighter -> {
                        if (f.getResult() == FightResult.FIGHTER_1_WIN) {
                            stats.get(fighter).fightWinCount++;
                            if (condition.predicate.test(f)) {
                                stats.get(fighter).fightMatching++;
                            }
                        }
                    });

                    fighter2.ifPresent(fighter -> {
                        if (f.getResult() == FightResult.FIGHTER_2_WIN) {
                            stats.get(fighter).fightWinCount++;
                            if (condition.predicate.test(f)) {
                                stats.get(fighter).fightMatching++;
                            }
                        }
                    });


                });


        List<FighterStat> fighterstats = stats.values()
                .stream()
                .filter(fs -> fs.fighter.trim().length() > 0)
                .filter(fs -> fs.fightWinCount >= 10)
                .sorted(Comparator.comparing(FighterStat::getPercentage).reversed().thenComparing((fs1, fs2) -> Double.compare(fs2.fightWinCount, fs1.fightWinCount)))
                .limit(100)
                .peek(fs-> logger.info("{} -> {}/{} -> {}%", fs.fighter, fs.fightMatching, fs.fightWinCount, fs.getPercentage()))
                .collect(Collectors.toList());

        if (fighterstats.size() > 0) {
            int reference = fighterstats.get(0).getPercentage();


            return fighterstats.stream()
                    .map(fs -> {
                        StatsEntry entry = new StatsEntry();

                        MmathFighter f = new MmathFighter();
                        f.setSherdogUrl(fs.fighter);
                        entry.setFighter(f);
                        entry.setTextToShow(String.format(condition.shortDescription, (int) fs.fightMatching, fs.getPercentage()));


                        double percent = ((double) fs.getPercentage() / (double) reference) * 100;
                        entry.setPercent((int) percent);

                        return entry;
                    })
                    .collect(Collectors.toList());
        }


        return new ArrayList<>();
    }


    private class FighterStat {
        public double fightWinCount, fightMatching;
        public final String fighter;

        public FighterStat(String fighter) {
            this.fighter = fighter;
        }

        public double getFightWinCount() {
            return fightWinCount;
        }

        public double getFightMatching() {
            return fightMatching;
        }

        public int getPercentage() {
            return (int) ((fightMatching / fightWinCount) * 100);
        }


    }
}
