package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathOrganization;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.ftpix.mmath.dsl.Tables.EVENTS;
import static com.ftpix.mmath.dsl.Tables.ORGANIZATIONS;

@Component
public class EventDAO extends DAO<MmathEvent, String> {


    @Autowired
    private JdbcTemplate template;

    private final RecordMapper<Record, MmathEvent> recordMapper = r -> {
        MmathEvent e = new MmathEvent();
        e.setSherdogUrl(r.get(EVENTS.SHERDOGURL));
        Optional.ofNullable(r.get(EVENTS.DATE)).ifPresent(s -> {
            e.setDate(ZonedDateTime.ofInstant(s.toLocalDateTime().toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
        });

        MmathOrganization org = new MmathOrganization();
        org.setSherdogUrl(r.get(EVENTS.ORGANIZATION_ID));
        e.setOrganization(org);

        e.setName(r.get(EVENTS.NAME));
//        e.setLocation(EVENTS.LOCATION);
        e.setLastUpdate(r.get(EVENTS.LASTUPDATE).toLocalDateTime());
        e.setLastContentHash(r.get(EVENTS.CONTENTHASH));

        Optional.ofNullable(r.get(EVENTS.LASTCONTENTCHECK))
                .map(Timestamp::toLocalDateTime)
                .ifPresent(e::setLastCheck);
        return e;
    };


    @Override
    public MmathEvent getById(String id) {
        return getDsl().select().from(EVENTS)
                .where(EVENTS.SHERDOGURL.eq(id))
                .fetchOne(recordMapper);
    }


    public MmathEvent getFromHash(String hash) {
        return getDsl().select().from(EVENTS)
                .where(DSL.md5(EVENTS.SHERDOGURL).eq(hash))
                .fetchOne(recordMapper);
    }


    @Override
    public List<MmathEvent> getAll() {
        return getDsl().select().from(EVENTS)
                .fetch(recordMapper);
    }

    @Override
    public List<MmathEvent> getBatch(int offset, int limit) {
        return getDsl().select().from(EVENTS)
                .offset(offset)
                .limit(limit)
                .fetch(recordMapper);
    }

    public List<MmathEvent> getIncoming(String organizations, Integer page) {
        int limit = 20;

        if (page == null) {
            page = 1;
        }

        int offset = (page - 1) * limit;

        String[] orgsArray;
        if (organizations == null) {
            orgsArray = new String[0];
        } else {
            orgsArray = organizations.split(",");
        }

        SelectConditionStep<Record> select = getDsl().select().from(EVENTS)
                .where(EVENTS.DATE.ge(DSL.now()));

        if (orgsArray.length > 0) {
            select = select.and(DSL.md5(EVENTS.ORGANIZATION_ID).in(orgsArray));
        }

        return select.orderBy(EVENTS.DATE)
                .limit(limit)
                .offset(offset)
                .fetch(recordMapper);
    }


    public boolean deleteNotHappenedEvents() {
        return getDsl().delete(EVENTS)
                .where(EVENTS.DATE.ge(DSL.now()))
                .execute() >= 0;
    }

    @Override
    public String insert(MmathEvent e) {

        getDsl().insertInto(EVENTS)
                .set(EVENTS.SHERDOGURL, e.getSherdogUrl())
                .set(EVENTS.DATE, new Timestamp(e.getDate().toInstant().toEpochMilli()))
                .set(EVENTS.ORGANIZATION_ID, e.getOrganization().getSherdogUrl())
                .set(EVENTS.NAME, e.getName())
                .set(EVENTS.LASTUPDATE, DSL.now())
                .set(EVENTS.CONTENTHASH, e.getLastContentHash())
                .set(EVENTS.LASTCONTENTCHECK, e.getLastCheck() != null ? Timestamp.valueOf(e.getLastCheck()) : null)
                .execute();

        return e.getSherdogUrl();
    }

    @Override
    public boolean update(MmathEvent e) {
        return getDsl().update(EVENTS)
                .set(EVENTS.DATE, new Timestamp(e.getDate().toInstant().toEpochMilli()))
                .set(EVENTS.ORGANIZATION_ID, e.getOrganization().getSherdogUrl())
                .set(EVENTS.NAME, e.getName())
                .set(EVENTS.LASTUPDATE, DSL.now())
                .set(EVENTS.CONTENTHASH, e.getLastContentHash())
                .set(EVENTS.LASTCONTENTCHECK, e.getLastCheck() != null ? Timestamp.valueOf(e.getLastCheck()) : null)
                .where(EVENTS.SHERDOGURL.eq(e.getSherdogUrl()))
                .execute() == 1;
    }

    /**
     * Gets event to check for update, it is either happening today or has not yet completed it's webwatcher round
     * @return
     */
    public List<MmathEvent> getEventsToCheck() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return getDsl().select().from(EVENTS)
                .where(EVENTS.DATE.like(date + '%').or(EVENTS.CONTENTHASH.isNotNull()))
                .fetch(recordMapper);
    }

    @Override
    public boolean deleteById(String id) {
        return getDsl().delete(EVENTS).where(EVENTS.SHERDOGURL.eq(id)).execute() == 1;
    }

}
