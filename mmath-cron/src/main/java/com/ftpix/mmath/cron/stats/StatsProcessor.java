package com.ftpix.mmath.cron.stats;

import com.ftpix.mmath.dao.mysql.StatsCategoryDAO;
import com.ftpix.mmath.dao.mysql.StatsEntryDAO;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class StatsProcessor {
    protected Log logger = LogFactory.getLog(this.getClass());


    @Autowired
    private StatsEntryDAO statsEntryDAO;

    @Autowired
    private StatsCategoryDAO statsCategoryDAO;

    public void process() {
        StatsCategory cat = getStatsCategory();

        logger.info("Generating stats for category " + cat.getId() + " -> " + cat.getName());
        statsCategoryDAO.insert(cat);

        List<StatsEntry> entries = generateEntries();

        logger.info("Deleting old entries");
        statsEntryDAO.deleteByCategory(cat.getId());

        logger.info("Inserting " + entries.size() + " entries for category " + cat.getId());

        AtomicInteger rank = new AtomicInteger(0);
        entries.stream()
                .sorted(Comparator.comparing(StatsEntry::getPercent).reversed())
                .limit(100)
                .forEach(e -> {
                    e.setCategory(cat);
                    e.setRank(rank.getAndIncrement());
                    statsEntryDAO.insert(e);
                });

        logger.info(cat.getId() + " done");
    }

    protected abstract StatsCategory getStatsCategory();

    protected abstract List<StatsEntry> generateEntries();

}
