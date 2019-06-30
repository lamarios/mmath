package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.stats.StatsCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;

import static com.ftpix.mmath.dsl.Tables.*;

@Component
public class StatsCategoryDAO extends DAO<StatsCategory, String> {
    @Autowired
    private JdbcTemplate template;
    private Logger logger = LogManager.getLogger();

    private final RecordMapper<Record, StatsCategory> recordMapper = r -> {
        StatsCategory c = new StatsCategory();
        c.setId(r.get(STATS_CATEGORIES.ID));
        c.setDescription(r.get(STATS_CATEGORIES.DESCRIPTION));
        c.setName(r.get(STATS_CATEGORIES.NAME));
        c.setOrder(r.get(STATS_CATEGORIES.ORDER));
        c.setLastUpdate(r.get(STATS_CATEGORIES.LASTUPDATE).toLocalDateTime());
        return c;
    };


    public StatsCategoryDAO(JdbcTemplate template) {

        this.template = template;
    }


    @Override
    @PostConstruct
    public void init() {
        try {
            String createTable = "CREATE TABLE IF NOT EXISTS stats_categories\n" +
                    "(\n" +
                    "  id          VARCHAR(255) NOT NULL\n" +
                    "    PRIMARY KEY,\n" +
                    "  name        VARCHAR(255) NULL,\n" +
                    "  description TEXT         NULL,\n" +
                    "  lastUpdate  DATETIME     NULL\n" +
                    ")\n" +
                    "  ENGINE = InnoDB;";

            template.execute(createTable);
        } catch (Exception e) {
            logger.warn("Table probably already exist", e);
        }

        try {
            String addRankQuery = "ALTER TABLE stats_categories ADD COLUMN `order` INT NOT NULL DEFAULT  0 AFTER description";
            template.execute(addRankQuery);
        } catch (Exception e) {
            logger.warn("Column probably already exist", e);
        }
    }

    @Override
    public StatsCategory getById(String id) {
        return getDsl().select().from(STATS_CATEGORIES)
                .where(STATS_CATEGORIES.ID.eq(id))
                .fetchOne(recordMapper);
    }

    @Override
    public List<StatsCategory> getAll() {
        return getDsl().select().from(STATS_CATEGORIES)
                .fetch(recordMapper);
    }

    @Override
    public List<StatsCategory> getBatch(int offset, int limit) {
        return getDsl().select().from(STATS_CATEGORIES)
                .limit(limit)
                .offset(offset)
                .fetch(recordMapper);
    }

    @Override
    public String insert(StatsCategory cat) {
        getDsl().insertInto(STATS_CATEGORIES, STATS_CATEGORIES.ID, STATS_CATEGORIES.NAME, STATS_CATEGORIES.DESCRIPTION, STATS_CATEGORIES.ORDER, STATS_CATEGORIES.LASTUPDATE)
                .values(cat.getId(), cat.getName(), cat.getDescription(), cat.getOrder(), new Timestamp(System.currentTimeMillis()))
                .onDuplicateKeyUpdate()
                .set(STATS_CATEGORIES.ID, cat.getId())
                .set(STATS_CATEGORIES.NAME, cat.getName())
                .set(STATS_CATEGORIES.DESCRIPTION, cat.getDescription())
                .set(STATS_CATEGORIES.LASTUPDATE, DSL.now())
                .execute();

        return cat.getId();
    }

    @Override
    public boolean update(StatsCategory object) {
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }
}
