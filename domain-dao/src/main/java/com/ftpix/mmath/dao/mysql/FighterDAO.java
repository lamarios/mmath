package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FighterDAO implements DAO<MmathFighter, String> {
    private final JdbcTemplate template;


    private final static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private RowMapper<MmathFighter> rowMapper = (resultSet, i) -> {
        MmathFighter f = new MmathFighter();
        f.setSherdogUrl(resultSet.getString("sherdogUrl"));
        f.setLastUpdate(LocalDateTime.parse(resultSet.getString("lastUpdate"), DAO.TIME_FORMAT));
        f.setName(resultSet.getString("name"));

        Optional.ofNullable(resultSet.getString("birthday")).ifPresent(s -> {
            f.setBirthday(LocalDate.parse(s, DAO.DATE_FORMAT));
        });
        f.setDraws(resultSet.getInt("draws"));
        f.setLosses(resultSet.getInt("losses"));
        f.setWins(resultSet.getInt("wins"));
        f.setWeight(resultSet.getString("weight"));
        f.setHeight(resultSet.getString("height"));
        f.setNickname(resultSet.getString("nickname"));
        f.setNc(resultSet.getInt("nc"));
        f.setSearchRank(resultSet.getInt("search_rank"));
        return f;
    };


    public FighterDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
    public void init() {
        try {
            String createTable = "CREATE TABLE IF NOT  EXISTS fighters\n" +
                    "(\n" +
                    "  sherdogUrl VARCHAR(1000) NOT NULL\n" +
                    "    PRIMARY KEY,\n" +
                    "  lastUpdate DATETIME      NULL,\n" +
                    "  name       VARCHAR(255)  NULL,\n" +
                    "  picture    VARCHAR(255)  NULL,\n" +
                    "  birthday   DATE      NULL,\n" +
                    "  draws      INT           NULL,\n" +
                    "  losses     INT           NULL,\n" +
                    "  wins       INT           NULL,\n" +
                    "  weight     VARCHAR(255)  NULL,\n" +
                    "  height     VARCHAR(255)  NULL,\n" +
                    "  nickname   VARCHAR(255)  NULL,\n" +
                    "  nc         INT           NULL\n" +
                    ")\n" +
                    "  ENGINE = InnoDB;\n";


            template.execute(createTable);
        } catch (Exception e) {
            //table probably already exist
        }


        try {
            template.execute("ALTER TABLE fighters ADD COLUMN search_rank INT(2) DEFAULT 99");
        } catch (Exception e) {
            //probably already exist
        }

        try {
            template.execute("ALTER TABLE fighters DROP COLUMN picture");
        } catch (Exception e) {
            //probably already removed
        }
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
        String sql = "INSERT INTO fighters (sherdogUrl, lastUpdate, name,  birthday, draws, losses, wins, weight, height, nickname, nc) VALUES (?,NOW(),?,?,?,?,?,?,?,?,?)";
        template.update(sql, f.getSherdogUrl(), f.getName(), f.getBirthday() == null ? null : DAO.DATE_FORMAT.format(f.getBirthday()), f.getDraws(), f.getLosses(), f.getWins(), f.getWeight(), f.getHeight(), f.getNickname(), f.getNc());
        return f.getSherdogUrl();
    }

    @Override
    public boolean update(MmathFighter f) {
        String sql = "UPDATE fighters SET  lastUpdate = NOW(), name = ?,  birthday = ?, draws = ?, losses = ?, wins = ?, weight = ?, height = ?, nickname = ?, nc = ? WHERE sherdogUrl = ?";
        return 1 == template.update(sql, f.getName(), f.getBirthday() == null ? null : DAO.DATE_FORMAT.format(f.getBirthday()), f.getDraws(), f.getLosses(), f.getWins(), f.getWeight(), f.getHeight(), f.getNickname(), f.getNc(), f.getSherdogUrl());
    }

    @Override
    public boolean deleteById(String id) {
        return template.update("DELETE FROM fighters WHERE sherdogUrl = ?", id) == 1;
    }

    /**
     * Search fighter by their name and nickname
     *
     * @param name
     * @return
     */
    public List<MmathFighter> searchByName(String name) {
        String searchQuery = "%" + name + "%";

        String[] nameSplit = name.split(" ");
        ExecutorService exec = Executors.newFixedThreadPool(nameSplit.length + 2);

        List<Future<List<MmathFighter>>> futures = new ArrayList<>();

        futures.add(exec.submit(() -> {
            String query = "SELECT * FROM fighters WHERE `name` LIKE ? ORDER BY search_rank LIMIT 10";
            return template.query(query, rowMapper, searchQuery);
        }));

        futures.add(exec.submit(() -> {
            String query2 = "SELECT * FROM fighters WHERE `nickname` LIKE ? ORDER BY search_rank LIMIT 10";
            return template.query(query2, rowMapper, searchQuery);
        }));

        if (nameSplit.length > 1) {
            futures.addAll(
                    Stream.of(nameSplit)
                            .map(s -> {
                                return exec.submit(() -> {
                                    String query2 = "SELECT * FROM fighters WHERE `nickname` LIKE ? OR `name` LIKE ? ORDER BY search_rank LIMIT 10";
                                    return template.query(query2, rowMapper, s, s);
                                });
                            }).collect(Collectors.toList())
            );
        }
        return futures.stream()
                .flatMap(f -> {
                    try {
                        return f.get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        return null;
                    }
                })
                .filter(s -> s != null)
                .filter(distinctByKey(MmathFighter::getSherdogUrl)).limit(10).collect(Collectors.toList());
    }

    /**
     * Will give each fighter a search rank based on the organisation they fought on. so a fighter that fought in the UFC will appear first in results as users are most likely to search for them
     *
     * @return
     */
    public boolean setAllFighterSearchRank() {
        template.update("UPDATE fighters fi\n" +
                "SET search_rank = (\n" +
                "  SELECT DISTINCT CASE e.organization_id\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2'\n" +
                "                    THEN 1\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/Dream-1357'\n" +
                "                    THEN 2\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/Strikeforce-716'\n" +
                "                    THEN 2\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/Pride-Fighting-Championships-3'\n" +
                "                    THEN 2\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/Bellator-MMA-1960'\n" +
                "                    THEN 2\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469'\n" +
                "                    THEN 3\n" +
                "                  WHEN 'https://www.sherdog.com/organizations/One-Championship-3877'\n" +
                "                    THEN 3\n" +
                "                  ELSE 99\n" +
                "                  END AS rank\n" +
                "  FROM fights f\n" +
                "    JOIN events e ON f.event_id = e.sherdogUrl\n" +
                "  WHERE f.fighter1_id = fi.sherdogUrl OR\n" +
                "        f.fighter2_id = fi.sherdogUrl\n" +
                "  ORDER BY rank ASC\n" +
                "  LIMIT 0, 1)");

        //some might have null values, setting it to 99
        template.update("UPDATE fighters SET search_rank = IF(search_rank IS NULL, 99, search_rank)");

        return true;
    }

    public MmathFighter getFromHash(String hash) {
        String query = "SELECT * FROM fighters WHERE MD5(sherdogUrl) = ?";

        List<MmathFighter> query1 = template.query(query, rowMapper, hash);

        return query1.size() == 1 ? query1.get(0) : null;
    }

}
