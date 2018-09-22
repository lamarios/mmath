package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.AggregatedHypeTrain;
import com.ftpix.mmath.model.HypeTrain;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HypeTrainDAO implements DAO<HypeTrain, HypeTrain> {

    private final JdbcTemplate template;

    public HypeTrainDAO(JdbcTemplate template) {
        this.template = template;
    }

    private final RowMapper<HypeTrain> rowMapper = (resultSet, i) -> {
        HypeTrain train = new HypeTrain(null, null);
        train.setFighterId(resultSet.getString("fighter"));
        train.setUser(resultSet.getString("user"));

        Optional.ofNullable(resultSet.getString("name"))
                .ifPresent(train::setFighterName);

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

        String sql = "REPLACE INTO hype_trains(user, fighter) VALUES (?,?)";
        template.update(sql, object.getUser(), object.getFighterId());
        return object;
    }

    @Override
    public boolean update(HypeTrain object) {
        throw new UnsupportedOperationException("Hype trains can't be updated");
    }

    @Override
    public boolean deleteById(HypeTrain id) {
        return template.update("DELETE FROM hype_trains WHERE fighter = ? AND user = ?", id.getFighterId(), id.getUser()) == 1;
    }


    public List<HypeTrain> getFromFighterHash(String hash) {
        String query = "SELECT * FROM hype_trains WHERE MD5(fighter) = ?";

        return template.query(query, rowMapper, hash);
    }

    public List<HypeTrain> getByUser(String user) {
        String query = "SELECT t.*, f.name FROM hype_trains t LEFT JOIN fighters f ON t.fighter = f.sherdogUrl  WHERE user = ?";

        return template.query(query, rowMapper, user);
    }

    /**
     * Get top 10 hype train
     *
     * @return
     */
    public List<AggregatedHypeTrain> getTop() {

        String sql = "SELECT t.fighter as fighter, f.name as name, count(*) as count from hype_trains t LEFT JOIN fighters f ON t.fighter = f.sherdogUrl GROUP BY fighter ORDER BY count DESC LIMIT 20";

        RowMapper<AggregatedHypeTrain> mapper = (resultSet, i) -> {
            AggregatedHypeTrain trains = new AggregatedHypeTrain();

            trains.setCount(resultSet.getInt("count"));
            trains.setFighter(resultSet.getString("fighter"));
            trains.setName(resultSet.getString("name"));
            return trains;
        };


        return template.query(sql, mapper);
    }


    /**
     * Checks whether a user is on bvoard the train
     * @param user
     * @param fighterUrl
     * @return
     */
    public boolean isOnBoard(String user, String fighterUrl) {
        String sql = "SELECT * FROM hype_trains WHERE user= ? AND fighter= ?";

        List<Map<String, Object>> maps = template.queryForList(sql, user, fighterUrl);

        return maps.size() == 1;
    }


    /**
     * count the number of people on board  for a given fighter
     * @param fighter sherdog url
     * @return hoe many people on board
     */
    public long countForFighter(String fighter){
        String sql = "SELECT COUNT(1) as count FROM hype_trains WHERE fighter = ?";

        List<Map<String, Object>> result = template.queryForList(sql, fighter);

        if(result.size() == 1){
            return (long) result.get(0).get("count");
        }else{
            return 0;
        }

    }

}
