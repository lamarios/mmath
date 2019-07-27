package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.AggregatedHypeTrain;
import com.ftpix.mmath.model.HypeTrain;
import com.ftpix.mmath.model.HypeTrainStats;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.ftpix.mmath.dsl.Tables.*;

@Component
public class HypeTrainDAO extends DAO<HypeTrain, HypeTrain> {

    @Autowired
    private JdbcTemplate template;

    public HypeTrainDAO(JdbcTemplate template) {
        this.template = template;
    }

    private final RecordMapper<Record, HypeTrainStats> statsRecordMapper = r -> {
        HypeTrainStats stats = new HypeTrainStats();

        stats.setFighter(r.get(HYPE_TRAINS_STATS.FIGHTER));
        stats.setMonth(r.get(HYPE_TRAINS_STATS.MONTH));
        stats.setCount(r.get(HYPE_TRAINS_STATS.COUNT));

        return stats;
    };


    private final RecordMapper<Record, HypeTrain> recordMapper = r -> {

        HypeTrain train = new HypeTrain(null, null);
        train.setFighterId(r.get(HYPE_TRAINS.FIGHTER));
        train.setUser(r.get(HYPE_TRAINS.USER));
        train.setNextFight(r.get(HYPE_TRAINS.NEXTFIGHT));
        train.setNotified(r.get(HYPE_TRAINS.NOTIFIED) == 1);

        Optional.ofNullable(r.get(FIGHTERS.NAME)).ifPresent(train::setFighterName);

        return train;
    };


    @Override
    @PostConstruct
    public void init() {
        try {
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
        } catch (Exception e) {

        }


        try {
            String alterTable = "alter table hype_trains add nextFight BIGINT null;";

            template.execute(alterTable);
            alterTable = "alter table hype_trains add notified BOOLEAN default FALSE null;";
            template.execute(alterTable);

            alterTable = "alter table hype_trains add constraint hype_trains_fights_id_fk foreign key (nextFight) references fights (id);";
            template.execute(alterTable);
        } catch (Exception e) {

        }


    }

    @Override
    public HypeTrain getById(HypeTrain id) {
        throw new UnsupportedOperationException("This method shouldn't be used");
    }

    @Override
    public List<HypeTrain> getAll() {
        return getDsl().select().from(HYPE_TRAINS)
                .fetch(recordMapper);
    }

    @Override
    public List<HypeTrain> getBatch(int offset, int limit) {
        return getDsl().select().from(HYPE_TRAINS)
                .offset(offset)
                .limit(limit)
                .fetch(recordMapper);
    }

   public List<String> getAllUsers(int offset, int limit){
       return getDsl().selectDistinct(HYPE_TRAINS.USER).from(HYPE_TRAINS)
               .offset(offset)
               .limit(limit)
               .fetch(record -> record.get(HYPE_TRAINS.USER, String.class));
   }

    @Override
    public HypeTrain insert(HypeTrain object) {

        getDsl().insertInto(HYPE_TRAINS, HYPE_TRAINS.USER, HYPE_TRAINS.FIGHTER)
                .values(object.getUser(), object.getFighterId())
                .onDuplicateKeyIgnore()
                .execute();

        return object;
    }

    @Override
    public boolean update(HypeTrain ht) {
        return getDsl().update(HYPE_TRAINS)
                .set(HYPE_TRAINS.NEXTFIGHT, ht.getNextFight())
                .set(HYPE_TRAINS.NOTIFIED, ht.isNotified() ? (byte) 1 : (byte) 0)
                .where(HYPE_TRAINS.FIGHTER.eq(ht.getFighterId()))
                .and(HYPE_TRAINS.USER.eq(ht.getUser()))
                .execute() == 1;
    }


    @Override
    public boolean deleteById(HypeTrain id) {
        return getDsl().delete(HYPE_TRAINS)
                .where(HYPE_TRAINS.FIGHTER.eq(id.getFighterId()))
                .and(HYPE_TRAINS.USER.eq(id.getUser()))
                .execute() == 1;
    }


    public List<HypeTrain> getFromFighterHash(String hash) {
        return getDsl().select().from(HYPE_TRAINS).where(DSL.md5(HYPE_TRAINS.FIGHTER).eq(hash))
                .fetch(recordMapper);
    }

    public List<HypeTrain> getByUser(String user) {
        return getDsl().select(HYPE_TRAINS.asterisk(), FIGHTERS.NAME).from(HYPE_TRAINS)
                .leftJoin(FIGHTERS).on(HYPE_TRAINS.FIGHTER.eq(FIGHTERS.SHERDOGURL))
                .where(HYPE_TRAINS.USER.eq(user))
                .fetch(recordMapper);
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


        Field<Integer> count = DSL.count().as("count");

        return getDsl().select(HYPE_TRAINS.FIGHTER, FIGHTERS.NAME, count)
                .from(HYPE_TRAINS)
                .leftJoin(FIGHTERS).on(HYPE_TRAINS.FIGHTER.eq(FIGHTERS.SHERDOGURL))
                .groupBy(HYPE_TRAINS.FIGHTER)
                .orderBy(count.desc())
                .offset(offset)
                .limit(topLimit)
                .fetch(r -> {
                    AggregatedHypeTrain trains = new AggregatedHypeTrain();

                    trains.setCount(r.get(count));
                    trains.setFighter(r.get(HYPE_TRAINS.FIGHTER));
                    trains.setName(r.get(FIGHTERS.NAME));
                    return trains;
                });

//        String sql = "SELECT t.fighter as fighter, f.name as name, count(*) as count from hype_trains t LEFT JOIN fighters f ON t.fighter = f.sherdogUrl GROUP BY fighter ORDER BY count DESC LIMIT ?,?";
//
//        RowMapper<AggregatedHypeTrain> mapper = (resultSet, i) -> {
//            AggregatedHypeTrain trains = new AggregatedHypeTrain();
//
//            trains.setCount(resultSet.getInt("count"));
//            trains.setFighter(resultSet.getString("fighter"));
//            trains.setName(resultSet.getString("name"));
//            return trains;
//        };


//        return template.query(sql, mapper, offset, topLimit);

    }


    /**
     * Checks whether a user is on bvoard the train
     *
     * @param user
     * @param fighterUrl
     * @return
     */
    public boolean isOnBoard(String user, String fighterUrl) {
        return getDsl().selectCount().from(HYPE_TRAINS)
                .where(HYPE_TRAINS.USER.eq(user))
                .and(HYPE_TRAINS.FIGHTER.eq(fighterUrl))
                .fetchOne(0, int.class) == 1;
//        String sql = "SELECT * FROM hype_trains WHERE user= ? AND fighter= ?";
//
//        List<Map<String, Object>> maps = template.queryForList(sql, user, fighterUrl);
//
//        return maps.size() == 1;
    }


    /**
     * count the number of people on board  for a given fighter
     *
     * @param fighter sherdog url
     * @return hoe many people on board
     */
    public long countForFighter(String fighter) {
        return getDsl().selectCount().from(HYPE_TRAINS)
                .where(HYPE_TRAINS.FIGHTER.eq(fighter))
                .fetchOne(0, long.class);
    }

    /**
     * gets the stats for a fighter
     *
     * @param fighterHash to get stats from
     * @param monthCount  how many month we want
     * @return
     */
    public List<HypeTrainStats> getStats(String fighterHash, int monthCount) {
        return getDsl().select().from(HYPE_TRAINS_STATS)
                .where(DSL.md5(HYPE_TRAINS_STATS.FIGHTER).eq(fighterHash))
                .orderBy(HYPE_TRAINS_STATS.MONTH.desc())
                .limit(monthCount)
                .fetch(statsRecordMapper);
    }


    public HypeTrainStats insertStats(HypeTrainStats stats) {

        getDsl().insertInto(HYPE_TRAINS_STATS, HYPE_TRAINS_STATS.MONTH, HYPE_TRAINS_STATS.FIGHTER, HYPE_TRAINS_STATS.COUNT)
                .values(stats.getMonth(), stats.getFighter(), stats.getCount())
                .onDuplicateKeyUpdate()
                .set(HYPE_TRAINS_STATS.MONTH, stats.getMonth())
                .set(HYPE_TRAINS_STATS.FIGHTER, stats.getFighter())
                .set(HYPE_TRAINS_STATS.COUNT, stats.getCount())
                .execute();

        return stats;
    }


}
