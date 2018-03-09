package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.stats.StatsCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.List;

public class StatsCategoryDAO implements DAO<StatsCategory, String> {
    private final JdbcTemplate template;

    private final RowMapper<StatsCategory> rowMapper = (rs, i) -> {
        StatsCategory c = new StatsCategory();
        c.setId(rs.getString("id"));
        c.setDescription(rs.getString("description"));
        c.setName(rs.getString("name"));
        c.setLastUpdate(LocalDateTime.parse(rs.getString("lastUpdate"), DAO.TIME_FORMAT));
        return c;
    };


    public StatsCategoryDAO(JdbcTemplate template) {

        this.template = template;
    }


    @Override
    public void init() {
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
    }

    @Override
    public StatsCategory getById(String id) {
        String query = "SELECT * FROM stats_categories WHERE id = ?";

        List<StatsCategory> query1 = template.query(query, rowMapper, id);

        return query1.size() == 1 ? query1.get(0) : null;
    }

    @Override
    public List<StatsCategory> getAll() {
        String query = "SELECT * FROM stats_categories";

        List<StatsCategory> query1 = template.query(query, rowMapper);

        return query1;
    }

    @Override
    public String insert(StatsCategory cat) {
        template.update("REPLACE INTO  stats_categories (id, `name`, description, lastUpdate) VALUES (?,?,?, NOW())",
                cat.getId(),
                cat.getName(),
                cat.getDescription());

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
