package com.ftpix.mmath.cacheslave.stats;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class StatsProcessor {
    protected final MySQLDao dao;
    protected Logger logger = LogManager.getLogger();

    public StatsProcessor(MySQLDao dao) {
        this.dao = dao;
    }


    public void process() {
        StatsCategory cat = getStatsCategory();

        logger.info("Generating stats for category {} -> {}", cat.getId(), cat.getName());
        dao.getStatsCategoryDAO().insert(cat);

        List<StatsEntry> entries = generateEntries();

        logger.info("Deleting old entries");
        dao.getStatsEntryDAO().deleteByCategory(cat.getId());

        logger.info("Inserting {} entries for category {}", entries.size(), cat.getId());

        AtomicInteger rank = new AtomicInteger(0);
        entries.stream()
                .sorted(Comparator.comparing(StatsEntry::getPercent).reversed())
                .limit(100)
                .forEach(e -> {
                    e.setCategory(cat);
                    e.setRank(rank.getAndIncrement());
                    dao.getStatsEntryDAO().insert(e);
                });

        logger.info("{} done", cat.getId());
    }

    protected abstract StatsCategory getStatsCategory();

    protected abstract List<StatsEntry> generateEntries();

}
