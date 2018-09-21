package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.HypeTrain;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public class HypeTrainDAO implements DAO<HypeTrain, HypeTrain> {

    private final JdbcTemplate template;

    public HypeTrainDAO(JdbcTemplate template) {
        this.template = template;
    }

    private final RowMapper<HypeTrain> rowMapper = (resultSet, i) -> {
        HypeTrain train = new HypeTrain(null, null);
        train.setFighterId(resultSet.getString("fighter"));
        train.setUser(resultSet.getString("user"));
        return train;
    };

    @Override
    public void init() {
        String createTable = "CREATE TABLE hype_trains\n" +
                "(\n" +
                "    user VARCHAR(255) NOT NULL,\n" +
                "    fighter VARCHAR(1000) NOT NULL,\n" +
                "    CONSTRAINT hype_trains_pk PRIMARY KEY (user, fighter)\n" +
                ");";

        template.execute(createTable);


    }

    @Override
    public HypeTrain getById(HypeTrain id) {
        throw new UnsupportedOperationException("This method shouldn't be used");
    }

    @Override
    public List<HypeTrain> getAll() {
        String query = "SELECT * FROM hype_trains";

        return template.query(query, rowMapper);
    }

    @Override
    public HypeTrain insert(HypeTrain object) {

        String sql = "INSERT INTO hype_trains(user, fighter) VALUES (?,?)";
        template.update(sql, object.getUser(), object.getFighterId());
        return object;
    }

    @Override
    public boolean update(HypeTrain object) {
        throw new UnsupportedOperationException("Hype trains can't be updated");
    }

    @Override
    public boolean deleteById(HypeTrain id) {
        return template.update("DELETE FROM hype_trains WHERE figher = ? AND user = ?", id.getFighterId(), id.getUser()) == 1;
    }


    public List<HypeTrain> getFromFighterHash(String hash) {
        String query = "SELECT * FROM hype_trains WHERE MD5(fighter) = ?";

        return template.query(query, rowMapper, hash);
    }

    public List<HypeTrain> getByUser(String user) {
        String query = "SELECT * FROM hype_trains WHERE MD5(fighter) = ?";

        return template.query(query, rowMapper, user);
    }
}
