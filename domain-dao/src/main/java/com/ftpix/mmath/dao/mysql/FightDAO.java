package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ftpix.mmath.dsl.Tables.*;

@Component
public class FightDAO extends DAO<MmathFight, Long> {


    RecordMapper<Record, MmathFight> recordMapper = r -> {
        MmathFight f = new MmathFight();
        f.setId(r.get(FIGHTS.ID));

        Optional.ofNullable(r.get(FIGHTS.FIGHTER1_ID)).ifPresent(url -> {
            MmathFighter f1 = new MmathFighter();
            f1.setSherdogUrl(url);
            f.setFighter1(f1);
        });
        Optional.ofNullable(r.get(FIGHTS.FIGHTER2_ID)).ifPresent(url -> {
            MmathFighter f2 = new MmathFighter();
            f2.setSherdogUrl(url);
            f.setFighter2(f2);
        });

        MmathEvent e = new MmathEvent();
        e.setSherdogUrl(r.get(FIGHTS.EVENT_ID));
        f.setEvent(e);

        Optional.ofNullable(r.get(FIGHTS.DATE)).ifPresent(s -> {
            e.setDate(ZonedDateTime.ofInstant(s.toLocalDateTime().toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
        });

        Optional.ofNullable(r.get(FIGHTS.RESULT)).ifPresent(w -> {
            try {
                f.setResult(FightResult.valueOf(w));
            } catch (Exception ex) {

            }
        });

        f.setWinMethod(r.get(FIGHTS.WINMETHOD));
        f.setWinTime(r.get(FIGHTS.WINTIME));
        f.setWinRound(r.get(FIGHTS.WINROUND));
        f.setLastUpdate(r.get(FIGHTS.LASTUPDATE).toLocalDateTime());
        f.setFightType(FightType.valueOf(r.get(FIGHTS.FIGHT_TYPE)));

        return f;
    };


    @Override
    @PostConstruct
    public void init() {

        String createTable = "CREATE TABLE IF NOT EXISTS fights\n" +
                "(\n" +
                "  id          BIGINT AUTO_INCREMENT\n" +
                "    PRIMARY KEY,\n" +
                "  fighter1_id VARCHAR(1000) NULL,\n" +
                "  fighter2_id VARCHAR(1000) NULL,\n" +
                "  event_id    VARCHAR(1000) NULL,\n" +
                "  `date`        DATETIME      NULL,\n" +
                "  result      VARCHAR(100)  NULL,\n" +
                "  winMethod   VARCHAR(255)  NULL,\n" +
                "  winTime     VARCHAR(255)  NULL,\n" +
                "  winRound    INT           NULL,\n" +
                "  lastUpdate  DATETIME      NULL,\n" +
                "  CONSTRAINT primary_key\n" +
                "  UNIQUE (fighter1_id, fighter2_id, event_id)\n" +
                ")\n" +
                "  ENGINE = InnoDB;";

        template.execute(createTable);


        try {
            template.execute("ALTER TABLE fights ADD COLUMN fight_type VARCHAR(50) DEFAULT 'PRO'");
        } catch (Exception e) {

        }

    }

    @Override
    public MmathFight getById(Long id) {
        return getDsl().select().from(FIGHTS)
                .where(FIGHTS.ID.eq(id))
                .fetchOne(recordMapper);
    }

    @Override
    public List<MmathFight> getAll() {
        return getDsl().select().from(FIGHTS).fetch(recordMapper);
    }

    @Override
    public List<MmathFight> getBatch(int offset, int limit) {
        return getDsl().select().from(FIGHTS)
                .limit(limit)
                .offset(offset)
                .fetch(recordMapper);
    }

    public List<MmathFight> getFightsForEventHash(String hash) {
        return getDsl().select().from(FIGHTS)
                .where(DSL.md5(FIGHTS.EVENT_ID).eq(hash))
                .fetch(recordMapper);
    }

    @Override
    public Long insert(MmathFight f) {


        return getDsl().insertInto(FIGHTS)
                .set(FIGHTS.FIGHTER1_ID, Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.FIGHTER2_ID, Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.EVENT_ID, Optional.ofNullable(f.getEvent()).map(MmathEvent::getSherdogUrl).orElse(null))
                .set(FIGHTS.DATE, f.getDate() == null ? null: new Timestamp(f.getDate().toInstant().toEpochMilli()))
                .set(FIGHTS.RESULT, f.getResult().name())
                .set(FIGHTS.WINMETHOD, f.getWinMethod())
                .set(FIGHTS.WINTIME, f.getWinTime())
                .set(FIGHTS.WINROUND, f.getWinRound())
                .set(FIGHTS.FIGHT_TYPE, f.getFightType().name())
                .set(FIGHTS.LASTUPDATE, DSL.now())
                .returning(FIGHTS.ID)
                .fetchOne().get(FIGHTS.ID);
//        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
//
//        PreparedStatementCreator css = connection -> {
//            PreparedStatement s = connection.prepareStatement("INSERT  INTO fights (fighter1_id, fighter2_id, event_id, `date`, result, winMethod, winTime, winRound, fight_type, lastUpdate)" +
//                            "VALUES  (?,?,?,?,?,?,?,?,?,NOW())",
//                    Statement.RETURN_GENERATED_KEYS);
//            setStatement(s, f);
//            return s;

//        };

//        template.update(css, keyHolder);
//
//        return keyHolder.getKey().longValue();
    }

    /**
     * Deletes all fights for the triple fighter1, fighter2, event. Done to be able to insert a new one with more updated results
     *
     * @param fighter1 first fighter
     * @param fighter2 second fighter
     * @param event    event it happened
     * @return true if the delete query actually deleted something
     */
    public boolean deleteExistingSimilarFight(String fighter1, String fighter2, String event) {
        return getDsl().delete(FIGHTS)
                .where(
                        DSL.or(
                                DSL.and(FIGHTS.FIGHTER1_ID.eq(fighter1), FIGHTS.FIGHTER2_ID.eq(fighter2)),
                                DSL.and(FIGHTS.FIGHTER1_ID.eq(fighter2), FIGHTS.FIGHTER2_ID.eq(fighter1)))
                )
                .and(FIGHTS.EVENT_ID.eq(event))
                .execute() > 0;
//        return template.update("DELETE FROM fights WHERE ((fighter1_id = ? AND  fighter2_id = ?) OR (fighter2_id = ? AND  fighter1_id = ?)) AND event_id = ?", fighter1, fighter2, fighter1, fighter2, event) > 0;
    }

    @Override
    public boolean update(MmathFight f) {


        return getDsl().update(FIGHTS)
                .set(FIGHTS.FIGHTER1_ID, Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.FIGHTER2_ID, Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.EVENT_ID, Optional.ofNullable(f.getEvent()).map(MmathEvent::getSherdogUrl).orElse(null))
                .set(FIGHTS.DATE, f.getDate() == null ? null : new Timestamp(f.getDate().toInstant().toEpochMilli()))
                .set(FIGHTS.RESULT, f.getResult().name())
                .set(FIGHTS.WINMETHOD, f.getWinMethod())
                .set(FIGHTS.WINTIME, f.getWinTime())
                .set(FIGHTS.WINROUND, f.getWinRound())
                .set(FIGHTS.FIGHT_TYPE, f.getFightType().name())
                .set(FIGHTS.LASTUPDATE, DSL.now())
                .where(FIGHTS.ID.eq(f.getId()))
                .execute() == 1;

//        PreparedStatementCreator css = connection -> {
//            PreparedStatement s = connection.prepareStatement("UPDATE fights SET fighter1_id = ?, fighter2_id = ?, event_id = ?, `date` = ?, result = ?, winMethod = ?, winTime = ?, winRound = ?, fight_type = ?, lastUpdate = NOW() WHERE id = ?");
//            setStatement(s, f);
//            s.setLong(9, f.getId());
//            return s;
//
//        };
//
//        return template.update(css) == 1;
    }

    @Override
    public boolean deleteById(Long id) {
        return getDsl().delete(FIGHTS).where(FIGHTS.ID.eq(id)).execute() == 1;
    }


    public boolean deleteAllNotHappenedFights() {
        return getDsl().delete(FIGHTS).where(FIGHTS.RESULT.eq(FightResult.NOT_HAPPENED.name())).execute() > 0;
//        return template.update("DELETE FROM fights WHERE  result='NOT_HAPPENED'") >= 0;
    }

    public List<MmathFight> getByFighter(String sherdogUrl) {

//        String query = "SELECT * FROM fights WHERE fighter2_id = ? OR fighter1_id = ? ORDER BY `date` ASC";

        List<MmathFight> query1 = getDsl().select().from(FIGHTS)
                .where(FIGHTS.FIGHTER2_ID.eq(sherdogUrl))
                .or(FIGHTS.FIGHTER1_ID.eq(sherdogUrl))
                .orderBy(FIGHTS.DATE.asc())
                .fetch(recordMapper);

        List<MmathFight> fights = query1.stream()
                .filter(f -> Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).isPresent() && Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).isPresent())
                .map(f -> {
                    //we need to swap
                    if (f.getFighter2().getSherdogUrl().equalsIgnoreCase(sherdogUrl)) {
                        f.getFighter2().setSherdogUrl(f.getFighter1().getSherdogUrl());
                        f.getFighter1().setSherdogUrl(sherdogUrl);

                        switch (f.getResult()) {
                            case FIGHTER_1_WIN:
                                f.setResult(FightResult.FIGHTER_2_WIN);
                                break;
                            case FIGHTER_2_WIN:
                                f.setResult(FightResult.FIGHTER_1_WIN);
                                break;
                        }
                    }
                    return f;
                })
                .collect(Collectors.toList());
        return fights;
    }


    private void setStatement(PreparedStatement s, MmathFight f) throws SQLException {
        Optional<String> f1 = Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl);
        if (f1.isPresent()) {
            s.setString(1, f1.get());
        } else {
            s.setNull(1, Types.VARCHAR);
        }

        Optional<String> f2 = Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl);
        if (f2.isPresent()) {
            s.setString(2, f2.get());
        } else {
            s.setNull(2, Types.VARCHAR);
        }

        Optional<String> e = Optional.ofNullable(f.getEvent()).map(MmathEvent::getSherdogUrl);
        if (e.isPresent()) {
            s.setString(3, e.get());
        } else {
            s.setNull(3, Types.VARCHAR);
        }

        Optional<String> d = Optional.ofNullable(f.getDate()).map(DAO.TIME_FORMAT::format);
        if (d.isPresent()) {
            s.setString(4, d.get());
        } else {
            s.setNull(4, Types.TIME);
        }

        s.setString(5, f.getResult().toString());

        s.setString(6, f.getWinMethod());
        s.setString(7, f.getWinTime());
        s.setInt(8, f.getWinRound());
        s.setString(9, f.getFightType().toString());

    }

    public void replace(MmathFight f) {


        getDsl().insertInto(FIGHTS)
                .set(FIGHTS.FIGHTER1_ID, Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.FIGHTER2_ID, Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.EVENT_ID, Optional.ofNullable(f.getEvent()).map(MmathEvent::getSherdogUrl).orElse(null))
                .set(FIGHTS.DATE, f.getDate() == null ? null : new Timestamp(f.getDate().toInstant().toEpochMilli()))
                .set(FIGHTS.RESULT, f.getResult().name())
                .set(FIGHTS.WINMETHOD, f.getWinMethod())
                .set(FIGHTS.WINTIME, f.getWinTime())
                .set(FIGHTS.WINROUND, f.getWinRound())
                .set(FIGHTS.FIGHT_TYPE, f.getFightType().name())
                .set(FIGHTS.LASTUPDATE, DSL.now())
                .onDuplicateKeyUpdate()
                .set(FIGHTS.FIGHTER1_ID, Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.FIGHTER2_ID, Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).orElse(null))
                .set(FIGHTS.EVENT_ID, Optional.ofNullable(f.getEvent()).map(MmathEvent::getSherdogUrl).orElse(null))
                .set(FIGHTS.DATE, f.getDate() == null ? null : new Timestamp(f.getDate().toInstant().toEpochMilli()))
                .set(FIGHTS.RESULT, f.getResult().name())
                .set(FIGHTS.WINMETHOD, f.getWinMethod())
                .set(FIGHTS.WINTIME, f.getWinTime())
                .set(FIGHTS.WINROUND, f.getWinRound())
                .set(FIGHTS.FIGHT_TYPE, f.getFightType().name())
                .set(FIGHTS.LASTUPDATE, DSL.now())
                .returning(FIGHTS.ID)
                .fetchOne().get(FIGHTS.ID);


    }
}
