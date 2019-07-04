package com.ftpix.mmath.cron.stats.implementations;

import com.ftpix.mmath.cron.stats.StatsProcessor;
import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;



@Component
public class GlassCannonStats extends StatsProcessor {



    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private FighterDAO fighterDAO;

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

        BatchProcessor.forClass(MmathFighter.class, 100)
                .withSupplier((batch, batchSize, offset) -> fighterDAO.getBatch(offset, batchSize))
                .withProcessor(fighters -> {


                    top100.addAll(fighters.stream()
                            .filter(f -> f.getWinKo() >= 0 && f.getLossKo() >= 0 && f.getWinKo() == f.getWins() && f.getLossKo() == f.getLosses()) // only ko or tko wins/ losses
//                            .filter(f -> f.getWins()+f.getLosses() > 10) //with at least 10 fights
                            .collect(Collectors.toList())
                    );

                    logger.info("{} fighters with only KO or TKOs", top100.size());

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

                    f.setFights(fightDAO.getByFighter(f.getSherdogUrl()));
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
