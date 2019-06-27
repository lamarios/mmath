package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.AggregatedHypeTrain;
import com.ftpix.mmath.model.HypeTrain;
import com.ftpix.mmath.model.HypeTrainStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class HypeTrainDAO extends DAO<HypeTrain, HypeTrain> {

    @Autowired
    private JdbcTemplate template;

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


    private final RowMapper<HypeTrainStats> statsRowMapper = (resultSet, i) -> {
        HypeTrainStats stats = new HypeTrainStats();

        stats.setFighter(resultSet.getString("fighter"));
        stats.setMonth(resultSet.getString("month"));
        stats.setCount(resultSet.getInt("count"));

        return stats;
    };

    @Override
    @PostConstruct
    public void init() {
        String createTable = "CREATE TABLE IF NOT EXISTS hype_trains" +
                "(" +
                "    user VARCHAR(255) NOT NULL," +
                "    fighter VARCHAR(1000) NOT NULL," +
                "    CONSTRAINT hype_trains_pk PRIMARY KEY (user, fighter)" +
                ");";

        template.execute(createTable);


        createTable = "CREATE TABLE IF NOT EXISTS hype_trains_stats" +
                "(" +
                "    `month` VARCHAR (7) NOT NULL," +
                "    fighter VARCHAR(1000) NOT NULL," +
                "    `count` BIGINT NOT NULL DEFAULT 0, " +
                "    CONSTRAINT hype_trains_stats_pk PRIMARY KEY (`month`, fighter)" +
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
    public List<HypeTrain> getBatch(int offset, int limit) {
        String query = "SELECT * FROM hype_trains LIMIT ?,?";

        return template.query(query, new Integer[]{offset, limit}, rowMapper);
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
     * Get top 20 hype train
     *
     * @return
     */
    public List<AggregatedHypeTrain> getTop() {
        return getAllCounts(0, 20);
    }

    public List<AggregatedHypeTrain> getAllCounts(int offset, int topLimit) {
        String sql = "SELECT t.fighter as fighter, f.name as name, count(*) as count from hype_trains t LEFT JOIN fighters f ON t.fighter = f.sherdogUrl GROUP BY fighter ORDER BY count DESC LIMIT ?,?";

        RowMapper<AggregatedHypeTrain> mapper = (resultSet, i) -> {
            AggregatedHypeTrain trains = new AggregatedHypeTrain();

            trains.setCount(resultSet.getInt("count"));
            trains.setFighter(resultSet.getString("fighter"));
            trains.setName(resultSet.getString("name"));
            return trains;
        };


        return template.query(sql, mapper,  offset, topLimit);

    }


    /**
     * Checks whether a user is on bvoard the train
     *
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
     *
     * @param fighter sherdog url
     * @return hoe many people on board
     */
    public long countForFighter(String fighter) {
        String sql = "SELECT COUNT(1) as count FROM hype_trains WHERE fighter = ?";

        List<Map<String, Object>> result = template.queryForList(sql, fighter);

        if (result.size() == 1) {
            return (long) result.get(0).get("count");
        } else {
            return 0;
        }
    }

    /**
     * gets the stats for a fighter
     *
     * @param fighterHash to get stats from
     * @param monthCount  how many month we want
     * @return
     */
    public List<HypeTrainStats> getStats(String fighterHash, int monthCount) {
        String sql = "SELECT * FROM hype_trains_stats WHERE MD5(fighter) = ? ORDER BY `month` DESC LIMIT ?";

        return template.query(sql, statsRowMapper, fighterHash, monthCount);
    }


    public HypeTrainStats insertStats(HypeTrainStats stats) {
        String sql = "REPLACE INTO hype_trains_stats(`month`, fighter, `count`) VALUES (?,?,?)";
        template.update(sql, stats.getMonth(), stats.getFighter(), stats.getCount());
        return stats;
    }


}
