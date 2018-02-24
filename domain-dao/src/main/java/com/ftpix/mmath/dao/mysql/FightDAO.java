package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.FightResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FightDAO implements DAO<MmathFight, Long> {
    private final JdbcTemplate template;
    private final RowMapper<MmathFight> rowMapper = (rs, i) -> {
        MmathFight f = new MmathFight();
        f.setId(rs.getLong("id"));

        Optional.ofNullable(rs.getString("fighter1_id")).ifPresent(url -> {
            MmathFighter f1 = new MmathFighter();
            f1.setSherdogUrl(url);
            f.setFighter1(f1);
        });
        Optional.ofNullable(rs.getString("fighter2_id")).ifPresent(url -> {
            MmathFighter f2 = new MmathFighter();
            f2.setSherdogUrl(url);
            f.setFighter2(f2);
        });

        MmathEvent e = new MmathEvent();
        e.setSherdogUrl(rs.getString("event_id"));
        f.setEvent(e);

        Optional.ofNullable(rs.getString("date")).ifPresent(s -> {
            e.setDate(ZonedDateTime.parse(s, DAO.TIME_FORMAT));
        });

        Optional.ofNullable(rs.getString("result")).ifPresent(w -> {
            try {
                f.setResult(FightResult.valueOf(w));
            } catch (Exception ex) {

            }
        });

        f.setWinMethod(rs.getString("winMethod"));
        f.setWinTime(rs.getString("winTime"));
        f.setWinRound(rs.getInt("winRound"));
        f.setLastUpdate(LocalDateTime.parse(rs.getString("lastUpdate"), DAO.TIME_FORMAT));

        return f;
    };

    public FightDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
    public  void init() {
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
    }

    @Override
    public MmathFight getById(Long id) {
        String query = "SELECT * FROM fights WHERE id = ?";

        List<MmathFight> query1 = template.query(query, rowMapper, id);

        return query1.size() == 1 ? query1.get(0) : null;
    }

    @Override
    public List<MmathFight> getAll() {
        String query = "SELECT * FROM fights";

        return template.query(query, rowMapper);
    }

    @Override
    public Long insert(MmathFight f) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator css = connection -> {
            PreparedStatement s = connection.prepareStatement("INSERT  INTO fights (fighter1_id, fighter2_id, event_id, `date`, result, winMethod, winTime, winRound, lastUpdate)" +
                    "VALUES  (?,?,?,?,?,?,?,?,NOW())",
                    Statement.RETURN_GENERATED_KEYS);
            setStatement(s, f);
            return s;

        };

        template.update(css, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(MmathFight f) {
        PreparedStatementCreator css = connection -> {
            PreparedStatement s = connection.prepareStatement("UPDATE fights SET fighter1_id = ?, fighter2_id = ?, event_id = ?, `date` = ?, result = ?, winMethod = ?, winTime = ?, winRound = ?, lastUpdate = NOW() WHERE id = ?");
            setStatement(s, f);
            s.setLong(9, f.getId());
            return s;

        };

        return template.update(css) == 1;
    }

    @Override
    public boolean deleteById(Long id) {
        return template.update("DELETE  FROM fights WHERE  id = ?", id) == 1;
    }

    public List<MmathFight> getByFighter(String sherdogUrl) {

        String query = "SELECT * FROM fights WHERE fighter2_id = ? OR fighter1_id = ? ORDER BY `date` ASC";

        return template.query(query, rowMapper, sherdogUrl, sherdogUrl).stream()
                .filter(f-> Optional.ofNullable(f.getFighter1()).map(MmathFighter::getSherdogUrl).isPresent() && Optional.ofNullable(f.getFighter2()).map(MmathFighter::getSherdogUrl).isPresent())
                .map(f -> {
                    //we need to swap
                    if(f.getFighter2().getSherdogUrl().equalsIgnoreCase(sherdogUrl)){
                        f.getFighter2().setSherdogUrl(f.getFighter1().getSherdogUrl());
                        f.getFighter1().setSherdogUrl(sherdogUrl);

                        switch (f.getResult()){
                            case FIGHTER_1_WIN:
                                f.setResult(FightResult.FIGHTER_2_WIN);
                                break;
                            case FIGHTER_2_WIN:
                                f.setResult(FightResult.FIGHTER_1_WIN);
                                break;
                        }
                    }
                    return  f;
                }).collect(Collectors.toList());
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

    }
}
