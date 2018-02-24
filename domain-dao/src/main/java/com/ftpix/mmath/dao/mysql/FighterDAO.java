package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathFighter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.swing.text.html.Option;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FighterDAO implements DAO<MmathFighter, String> {
    private final JdbcTemplate template;

    private RowMapper<MmathFighter> rowMapper = (resultSet, i) -> {
        MmathFighter f = new MmathFighter();
        f.setSherdogUrl(resultSet.getString("sherdogUrl"));
        f.setLastUpdate(LocalDateTime.parse(resultSet.getString("lastUpdate"), DAO.TIME_FORMAT));
        f.setName(resultSet.getString("name"));
        f.setPicture(resultSet.getString("picture"));
        Optional.ofNullable(resultSet.getString("birthday")).ifPresent(s -> {
            f.setBirthday(LocalDate.parse(s, DAO.TIME_FORMAT));
        });
        f.setDraws(resultSet.getInt("draws"));
        f.setLosses(resultSet.getInt("losses"));
        f.setWins(resultSet.getInt("wins"));
        f.setWeight(resultSet.getString("weight"));
        f.setHeight(resultSet.getString("height"));
        f.setNickname(resultSet.getString("nickname"));
        f.setNc(resultSet.getInt("nc"));
        return f;
    };


    public FighterDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
    public String getCreateTableString() {
        return "CREATE TABLE fighters\n" +
                "(\n" +
                "  sherdogUrl VARCHAR(1000) NOT NULL\n" +
                "    PRIMARY KEY,\n" +
                "  lastUpdate DATETIME      NULL,\n" +
                "  name       VARCHAR(255)  NULL,\n" +
                "  picture    VARCHAR(255)  NULL,\n" +
                "  birthday   DATETIME      NULL,\n" +
                "  draws      INT           NULL,\n" +
                "  losses     INT           NULL,\n" +
                "  wins       INT           NULL,\n" +
                "  weight     VARCHAR(255)  NULL,\n" +
                "  height     VARCHAR(255)  NULL,\n" +
                "  nickname   VARCHAR(255)  NULL,\n" +
                "  nc         INT           NULL\n" +
                ")\n" +
                "  ENGINE = InnoDB;\n";
    }

    @Override
    public MmathFighter getById(String id) {
        String query = "SELECT * FROM fighters WHERE sherdogUrl = ?";

        List<MmathFighter> query1 = template.query(query, rowMapper, id);

        return query1.size() == 1 ? query1.get(0) : null;
    }

    @Override
    public List<MmathFighter> getAll() {
        String query = "SELECT * FROM fighters";

        return template.query(query, rowMapper);
    }

    @Override
    public String insert(MmathFighter f) {
        String sql = "INSERT INTO fighters (sherdogUrl, lastUpdate, name, picture, birthday, draws, losses, wins, weight, height, nickname, nc) VALUES (?,NOW(),?,?,?,?,?,?,?,?,?,?)";
        template.update(sql, f.getSherdogUrl(), f.getName(), f.getPicture(), f.getBirthday() == null ? "MULL" : DAO.TIME_FORMAT.format(f.getBirthday()), f.getDraws(), f.getLosses(), f.getWins(), f.getWeight(), f.getHeight(), f.getNickname(), f.getNc());
        return f.getSherdogUrl();
    }

    @Override
    public boolean update(MmathFighter f) {
        String sql = "UPDATE fighters SET  lastUpdate = NOW(), name = ?, picture = ?, birthday = ?, draws = ?, losses = ?, wins = ?, weight = ?, height = ?, nickname = ?, nc = ? WHERE sherdogUrl = ?";
        return 1 == template.update(sql, f.getName(), f.getPicture(), f.getBirthday() == null ? "MULL" : DAO.TIME_FORMAT.format(f.getBirthday()), f.getDraws(), f.getLosses(), f.getWins(), f.getWeight(), f.getHeight(), f.getNickname(), f.getNc(), f.getSherdogUrl());
    }

    @Override
    public boolean deleteById(String id) {
        return template.update("DELETE FROM fighters WHERE sherdogUrl = ?", id) == 1;
    }

    public List<MmathFighter> searchByName(String name) {
        String query = "SELECT * FROM fighters WHERE name LIKE ? LIMIT 5";

        List<MmathFighter> query1 = template.query(query, rowMapper, "%" + name + "%");

        return query1;
    }

    public MmathFighter getFromHash(String hash) {
        String query = "SELECT * FROM fighters WHERE MD5(sherdogUrl) = ?";

        List<MmathFighter> query1 = template.query(query, rowMapper, hash);

        return query1.size() == 1 ? query1.get(0) : null;
    }

}
