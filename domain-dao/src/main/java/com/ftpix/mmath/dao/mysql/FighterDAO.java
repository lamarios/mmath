package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathFighter;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ftpix.mmath.dsl.Tables.*;

@Component
public class FighterDAO extends DAO<MmathFighter, String> {


    private final static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    private RecordMapper<Record, MmathFighter> recordMapper = record -> {
        MmathFighter f = new MmathFighter();
        f.setSherdogUrl(record.get(FIGHTERS.SHERDOGURL));
        f.setLastUpdate(record.get(FIGHTERS.LASTUPDATE).toLocalDateTime());
        f.setName(record.get(FIGHTERS.NAME));

        Optional.ofNullable(record.get(FIGHTERS.BIRTHDAY)).ifPresent(s -> {
            f.setBirthday(s.toLocalDate());
        });
        f.setDraws(record.get(FIGHTERS.DRAWS));
        f.setLosses(record.get(FIGHTERS.LOSSES));
        f.setWins(record.get(FIGHTERS.WINS));
        f.setWeight(record.get(FIGHTERS.WEIGHT));
        f.setHeight(record.get(FIGHTERS.HEIGHT));
        f.setNickname(record.get(FIGHTERS.NICKNAME));
        f.setNc(record.get(FIGHTERS.NC));
        f.setSearchRank(record.get(FIGHTERS.SEARCH_RANK));

        f.setWinKo(record.get(FIGHTERS.WINKO));
        f.setWinSub(record.get(FIGHTERS.WINS));
        f.setWinDec(record.get(FIGHTERS.WINDEC));
        f.setLossKo(record.get(FIGHTERS.LOSSKO));
        f.setLossSub(record.get(FIGHTERS.LOSSSUB));
        f.setLossDec(record.get(FIGHTERS.LOSSDEC));

        return f;
    };


    @Override
    @PostConstruct
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

        try {
            template.execute("ALTER TABLE fighters ADD COLUMN winKo INT(4) DEFAULT 0");
            template.execute("ALTER TABLE fighters ADD COLUMN winSub INT(4) DEFAULT 0");
            template.execute("ALTER TABLE fighters ADD COLUMN winDec INT(4) DEFAULT 0");
            template.execute("ALTER TABLE fighters ADD COLUMN lossKo INT(4) DEFAULT 0");
            template.execute("ALTER TABLE fighters ADD COLUMN lossSub INT(4) DEFAULT 0");
            template.execute("ALTER TABLE fighters ADD COLUMN lossDec INT(4) DEFAULT 0");
        } catch (Exception e) {
            //probably already exists
        }


    }

    @Override
    public MmathFighter getById(String id) {
        return getDsl().select().from(FIGHTERS)
                .where(FIGHTERS.SHERDOGURL.eq(id))
                .fetchOne(recordMapper);
    }

    @Override
    public List<MmathFighter> getAll() {
        return getDsl().select().from(FIGHTERS).fetch(recordMapper);
    }

    public List<MmathFighter> getBatch(int offset, int limit) {
        return getDsl().select().from(FIGHTERS)
                .limit(limit)
                .offset(offset)
                .fetch(recordMapper);

    }


    @Override
    public String insert(MmathFighter f) {

        getDsl().insertInto(FIGHTERS)
                .set(FIGHTERS.SHERDOGURL, f.getSherdogUrl())
                .set(FIGHTERS.LASTUPDATE, DSL.now())
                .set(FIGHTERS.NAME, f.getName())
                .set(FIGHTERS.BIRTHDAY, f.getBirthday() == null ? null : Date.valueOf(f.getBirthday()))
                .set(FIGHTERS.DRAWS, f.getDraws())
                .set(FIGHTERS.LOSSES, f.getLosses())
                .set(FIGHTERS.WINS, f.getWins())
                .set(FIGHTERS.WEIGHT, f.getWeight())
                .set(FIGHTERS.HEIGHT, f.getHeight())
                .set(FIGHTERS.NICKNAME, f.getNickname())
                .set(FIGHTERS.NC, f.getNc())
                .set(FIGHTERS.WINKO, f.getWinKo())
                .set(FIGHTERS.WINSUB, f.getWinSub())
                .set(FIGHTERS.WINDEC, f.getWinDec())
                .set(FIGHTERS.LOSSKO, f.getLossKo())
                .set(FIGHTERS.LOSSSUB, f.getLossSub())
                .set(FIGHTERS.LOSSDEC, f.getLossDec())
                .execute();

        return f.getSherdogUrl();
    }

    @Override
    public boolean update(MmathFighter f) {

        return getDsl().update(FIGHTERS)
                .set(FIGHTERS.LASTUPDATE, DSL.now())
                .set(FIGHTERS.NAME, f.getName())
                .set(FIGHTERS.BIRTHDAY, f.getBirthday() == null ? null : Date.valueOf(f.getBirthday()))
                .set(FIGHTERS.DRAWS, f.getDraws())
                .set(FIGHTERS.LOSSES, f.getLosses())
                .set(FIGHTERS.WINS, f.getWins())
                .set(FIGHTERS.WEIGHT, f.getWeight())
                .set(FIGHTERS.HEIGHT, f.getHeight())
                .set(FIGHTERS.NICKNAME, f.getNickname())
                .set(FIGHTERS.NC, f.getNc())
                .set(FIGHTERS.WINKO, f.getWinKo())
                .set(FIGHTERS.WINSUB, f.getWinSub())
                .set(FIGHTERS.WINDEC, f.getWinDec())
                .set(FIGHTERS.LOSSKO, f.getLossKo())
                .set(FIGHTERS.LOSSSUB, f.getLossSub())
                .set(FIGHTERS.LOSSDEC, f.getLossDec())
                .where(FIGHTERS.SHERDOGURL.eq(f.getSherdogUrl()))
                .execute() == 1;

    }

    @Override
    public boolean deleteById(String id) {
        return getDsl().delete(FIGHTERS).where(FIGHTERS.SHERDOGURL.eq(id)).execute() == 1;
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

        futures.add(exec.submit(() -> getDsl().select().from(FIGHTERS)
                .where(FIGHTERS.NAME.like(searchQuery))
                .orderBy(FIGHTERS.SEARCH_RANK)
                .limit(10)
                .fetch(recordMapper)));

        futures.add(exec.submit(() -> getDsl().select().from(FIGHTERS)
                .where(FIGHTERS.NICKNAME.like(searchQuery))
                .orderBy(FIGHTERS.SEARCH_RANK)
                .limit(10)
                .fetch(recordMapper)));

        if (nameSplit.length > 1) {
            futures.addAll(
                    Stream.of(nameSplit)
                            .map(s -> exec.submit(() -> getDsl().select().from(FIGHTERS)
                                    .where(FIGHTERS.NAME.like(s))
                                    .or(FIGHTERS.NICKNAME.like(s))
                                    .orderBy(FIGHTERS.SEARCH_RANK)
                                    .limit(10)
                                    .fetch(recordMapper)
                            )).collect(Collectors.toList())
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


        Field<Integer> rank = DSL.choose(EVENTS.ORGANIZATION_ID)
                .when("https://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2", 1)
                .when("https://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2", 2)
                .when("https://www.sherdog.com/organizations/Dream-1357", 2)
                .when("https://www.sherdog.com/organizations/Strikeforce-716", 2)
                .when("https://www.sherdog.com/organizations/Pride-Fighting-Championships-3", 2)
                .when("https://www.sherdog.com/organizations/Bellator-MMA-1960", 2)
                .when("https://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469", 3)
                .when("https://www.sherdog.com/organizations/One-Championship-3877", 3)
                .otherwise(99)
                .as("rank");

        SelectLimitPercentStep<Record1<Integer>> subQuery = DSL.selectDistinct(rank).from(FIGHTS)
                .join(EVENTS).on(FIGHTS.EVENT_ID.eq(EVENTS.SHERDOGURL))
                .where(FIGHTS.FIGHTER1_ID.eq(FIGHTERS.SHERDOGURL))
                .or(FIGHTS.FIGHTER2_ID.eq(FIGHTERS.SHERDOGURL))
                .orderBy(rank)
                .limit(1);


        getDsl().update(FIGHTERS)
                .set(FIGHTERS.SEARCH_RANK, subQuery).execute();


//        template.update("UPDATE fighters fi\n" +
//                "SET search_rank = (\n" +
//                "  SELECT DISTINCT CASE e.organization_id\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2'\n" +
//                "                    THEN 1\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/Dream-1357'\n" +
//                "                    THEN 2\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/Strikeforce-716'\n" +
//                "                    THEN 2\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/Pride-Fighting-Championships-3'\n" +
//                "                    THEN 2\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/Bellator-MMA-1960'\n" +
//                "                    THEN 2\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469'\n" +
//                "                    THEN 3\n" +
//                "                  WHEN 'https://www.sherdog.com/organizations/One-Championship-3877'\n" +
//                "                    THEN 3\n" +
//                "                  ELSE 99\n" +
//                "                  END AS rank\n" +
//                "  FROM fights f\n" +
//                "    JOIN events e ON f.event_id = e.sherdogUrl\n" +
//                "  WHERE f.fighter1_id = fi.sherdogUrl OR\n" +
//                "        f.fighter2_id = fi.sherdogUrl\n" +
//                "  ORDER BY rank ASC\n" +
//                "  LIMIT 0, 1)");


        getDsl().update(FIGHTERS)
                .set(FIGHTERS.SEARCH_RANK, DSL.ifnull(99, FIGHTERS.SEARCH_RANK))
                .execute();

        return true;
    }

    public MmathFighter getFromHash(String hash) {
        return getDsl().select().from(FIGHTERS)
                .where(DSL.md5(FIGHTERS.SHERDOGURL).eq(hash))
                .fetchOne(recordMapper);
    }

}
