package com.ftpix.mmath.cron.stats;

import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class StatsProcessor {
    protected Logger logger = LogManager.getLogger();


    @Autowired
    private StatsEntryDAO statsEntryDAO;

    @Autowired
    private StatsCategoryDAO statsCategoryDAO;

    public void process() {
        StatsCategory cat = getStatsCategory();

        logger.info("Generating stats for category {} -> {}", cat.getId(), cat.getName());
        statsCategoryDAO.insert(cat);

        List<StatsEntry> entries = generateEntries();

        logger.info("Deleting old entries");
        statsEntryDAO.deleteByCategory(cat.getId());

        logger.info("Inserting {} entries for category {}", entries.size(), cat.getId());

        AtomicInteger rank = new AtomicInteger(0);
        entries.stream()
                .sorted(Comparator.comparing(StatsEntry::getPercent).reversed())
                .limit(100)
                .forEach(e -> {
                    e.setCategory(cat);
                    e.setRank(rank.getAndIncrement());
                    statsEntryDAO.insert(e);
                });

        logger.info("{} done", cat.getId());
    }

    protected abstract StatsCategory getStatsCategory();

    protected abstract List<StatsEntry> generateEntries();

}
