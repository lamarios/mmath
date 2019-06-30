package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;

import static com.ftpix.mmath.dsl.Tables.*;

@Component
public class StatsEntryDAO extends DAO<StatsEntry, Long> {
    @Autowired
    private JdbcTemplate template;
    private Logger logger = LogManager.getLogger();

    private final RecordMapper<Record, StatsEntry> recordMapper = r -> {
        StatsEntry s = new StatsEntry();

        StatsCategory cat = new StatsCategory();
        cat.setId(r.get(STATS_ENTRIES.CATEGORY_ID));
        s.setCategory(cat);

        s.setPercent(r.get(STATS_ENTRIES.PERCENT));
        s.setId(r.get(STATS_ENTRIES.ID));

        MmathFighter fighter = new MmathFighter();
        fighter.setSherdogUrl(r.get(STATS_ENTRIES.FIGHTER_ID));
        s.setFighter(fighter);


        s.setRank(r.get(STATS_ENTRIES.RANK));

        s.setTextToShow(r.get(STATS_ENTRIES.TEXT_TO_SHOW));
        s.setDetails(r.get(STATS_ENTRIES.DETAILS));
        s.setLastUpdate(r.get(STATS_ENTRIES.LASTUPDATE).toLocalDateTime());

        return s;
    };


    public StatsEntryDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
    @PostConstruct
    public void init() {

        try {
            String createTable = "CREATE TABLE IF NOT EXISTS  stats_entries\n" +
                    "(\n" +
                    "  id           INT(11) AUTO_INCREMENT\n" +
                    "    PRIMARY KEY,\n" +
                    "  category_id  VARCHAR(255) NULL,\n" +
                    "  fighter_id   VARCHAR(999) NULL,\n" +
                    "  percent      INT(3)       NULL,\n" +
                    "  text_to_show VARCHAR(255) NULL,\n" +
                    "  details      TEXT         NULL,\n" +
                    "  lastUpdate   DATETIME     NULL\n" +
                    ")\n" +
                    "  ENGINE = InnoDB;";


            template.execute(createTable);
        } catch (Exception e) {
            logger.warn("Table probably already exist", e);
        }

        try {
            String addRankQuery = "ALTER TABLE stats_entries ADD COLUMN rank INT NOT NULL DEFAULT  0 AFTER percent";
            template.execute(addRankQuery);
        } catch (Exception e) {
            logger.warn("Column probably already exist", e);
        }

    }

    @Override
    public StatsEntry getById(Long id) {

        return getDsl().select().from(STATS_ENTRIES)
                .where(STATS_ENTRIES.ID.eq(id.intValue()))
                .fetchOne(recordMapper);
    }

    @Override
    public List<StatsEntry> getAll() {
        return getDsl().select().from(STATS_ENTRIES)
                .fetch(recordMapper);
    }

    @Override
    public List<StatsEntry> getBatch(int offset, int limit) {
        return getDsl().select().from(STATS_ENTRIES)
                .limit(limit)
                .offset(offset)
                .fetch(recordMapper);
    }

    /**
     * Gets all the stats entries for a specific fighter, for the award feature
     *
     * @param hash the hash of the fighter's url
     * @return the list of stats entries
     */
    public List<StatsEntry> getForFighterHash(String hash) {
        return getDsl().select().from(STATS_ENTRIES)
                .where(DSL.md5(STATS_ENTRIES.FIGHTER_ID).eq(hash))
                .fetch(recordMapper);
    }


    public List<StatsEntry> getByCategory(String cat) {
        return getDsl().select().from(STATS_ENTRIES)
                .where(STATS_ENTRIES.CATEGORY_ID.eq(cat))
                .orderBy(STATS_ENTRIES.RANK)
                .fetch(recordMapper);
    }


    public boolean deleteByCategory(String category) {
        return getDsl().delete(STATS_ENTRIES).where(STATS_ENTRIES.CATEGORY_ID.eq(category)).execute() > 0;
    }

    @Override
    public Long insert(StatsEntry entry) {

        getDsl().insertInto(STATS_ENTRIES, STATS_ENTRIES.CATEGORY_ID, STATS_ENTRIES.FIGHTER_ID, STATS_ENTRIES.TEXT_TO_SHOW, STATS_ENTRIES.PERCENT, STATS_ENTRIES.DETAILS, STATS_ENTRIES.RANK, STATS_ENTRIES.LASTUPDATE)
                .values(entry.getCategory().getId(), entry.getFighter().getSherdogUrl(), entry.getTextToShow(), entry.getPercent(), entry.getDetails(), entry.getRank(), new Timestamp(System.currentTimeMillis()))
                .execute();

//        template.update("INSERT INTO stats_entries (category_id, fighter_id, text_to_show, percent, details, rank,lastUpdate) VALUES (?,?,?,?,?,?,NOW())",
//                entry.getCategory().getId(),
//                entry.getFighter().getSherdogUrl(),
//                entry.getTextToShow(),
//                entry.getPercent(),
//                entry.getDetails(),
//                entry.getRank()
//        );

        return 0L;
    }

    @Override
    public boolean update(StatsEntry object) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean deleteById(Long id) {
        throw new NotImplementedException("");
    }
}
