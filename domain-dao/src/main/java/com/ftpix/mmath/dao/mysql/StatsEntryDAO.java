package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.stats.StatsCategory;
import com.ftpix.mmath.model.stats.StatsEntry;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.List;

public class StatsEntryDAO implements DAO<StatsEntry, Long> {

    private final JdbcTemplate template;

    private final RowMapper<StatsEntry> rowMapper = (rs, i) -> {
        StatsEntry s = new StatsEntry();

        StatsCategory cat = new StatsCategory();
        cat.setId(rs.getString("category_id"));
        s.setCategory(cat);

        s.setPercent(rs.getInt("percent"));
        s.setId(rs.getLong("id"));

        MmathFighter fighter = new MmathFighter();
        fighter.setSherdogUrl(rs.getString("fighter_id"));
        s.setFighter(fighter);

        s.setTextToShow(rs.getString("text_to_show"));
        s.setDetails(rs.getString("details"));
        s.setLastUpdate(LocalDateTime.parse(rs.getString("lastUpdate"), DAO.TIME_FORMAT));

        return s;
    };


    public StatsEntryDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
    public void init() {
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

    }

    @Override
    public StatsEntry getById(Long id) {
        String query = "SELECT * FROM stats_entries WHERE id = ?";

        List<StatsEntry> query1 = template.query(query, rowMapper, id);

        return query1.size() == 1 ? query1.get(0) : null;
    }

    @Override
    public List<StatsEntry> getAll() {
        String query = "SELECT * FROM stats_entries ";

        List<StatsEntry> query1 = template.query(query, rowMapper);

        return query1;
    }


    public List<StatsEntry> getByCategory(String cat) {
        return template.query("SELECT * FROM stats_entries WHERE category_id = ? ORDER BY percent DESC ", rowMapper, cat);
    }


    public boolean deleteByCategory(String category) {
        return template.update("DELETE FROM stats_entries WHERE category_id = ?", category) > 0;
    }

    @Override
    public Long insert(StatsEntry entry) {
        template.update("INSERT INTO stats_entries (category_id, fighter_id, text_to_show, percent, details, lastUpdate) VALUES (?,?,?,?,?,NOW())",
                entry.getCategory().getId(),
                entry.getFighter().getSherdogUrl(),
                entry.getTextToShow(),
                entry.getPercent(),
                entry.getDetails()
        );

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
