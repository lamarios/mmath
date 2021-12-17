package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.stats.StatsCategory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

import static com.ftpix.mmath.dsl.Tables.STATS_CATEGORIES;

@Component
public class StatsCategoryDAO extends DAO<StatsCategory, String> {
    @Autowired
    private JdbcTemplate template;
    protected Log logger = LogFactory.getLog(this.getClass());

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
