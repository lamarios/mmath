package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.mmath.model.Utils;
import com.ftpix.sherdogparser.models.Organizations;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;

import static com.ftpix.mmath.dsl.Tables.ORGANIZATIONS;

@Component
public class OrganizationDAO extends DAO<MmathOrganization, String> {

    @Autowired
    private JdbcTemplate template;

    private final static String[] EVENT_FILTER_ORGANIZATIONS = new String[]{
            Utils.cleanUrl(Organizations.UFC.url),
            Utils.cleanUrl(Organizations.BELLATOR.url),
            Utils.cleanUrl(Organizations.INVICTA_FC.url),
            Utils.cleanUrl(Organizations.ONE_FC.url)
    };

    private final RecordMapper<Record, MmathOrganization>  recordMapper = r -> {
        MmathOrganization o = new MmathOrganization();
        o.setSherdogUrl(r.get(ORGANIZATIONS.SHERDOGURL));
        o.setLastUpdate(r.get(ORGANIZATIONS.LASTUPDATE).toLocalDateTime());
        o.setName(r.get(ORGANIZATIONS.NAME));
        return o;
    };

    public OrganizationDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
    @PostConstruct
    public void init() {
        String createTable = "CREATE TABLE IF NOT EXISTS organizations" +
                "(" +
                "  sherdogUrl VARCHAR(1000) NOT NULL" +
                "    PRIMARY KEY," +
                "  lastUpdate DATETIME      NULL," +
                "  name       VARCHAR(255)  NULL" +
                ")" +
                "  ENGINE = InnoDB;";

        template.execute(createTable);

    }

    @Override
    public MmathOrganization getById(String id) {
        return getDsl().select().from(ORGANIZATIONS)
                .where(ORGANIZATIONS.SHERDOGURL.eq(id))
                .fetchOne(recordMapper);
    }

    @Override
    public List<MmathOrganization> getAll() {
        return getDsl().select().from(ORGANIZATIONS)
                .fetch(recordMapper);
    }

    @Override
    public List<MmathOrganization> getBatch(int offset, int limit) {
        return getDsl().select().from(ORGANIZATIONS)
                .limit(limit)
                .offset(offset)
                .fetch(recordMapper);
    }

    @Override
    public String insert(MmathOrganization o) {

        getDsl().insertInto(ORGANIZATIONS, ORGANIZATIONS.SHERDOGURL, ORGANIZATIONS.LASTUPDATE, ORGANIZATIONS.NAME)
                .values(o.getSherdogUrl(), new Timestamp(System.currentTimeMillis()) , o.getName())
                .execute();

        return o.getSherdogUrl();
    }

    @Override
    public boolean update(MmathOrganization o) {
        return getDsl().update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, o.getName())
                .set(ORGANIZATIONS.LASTUPDATE, DSL.now())
                .where(ORGANIZATIONS.SHERDOGURL.eq(o.getSherdogUrl()))
                .execute() == 1;
    }

    @Override
    public boolean deleteById(String id) {
        return getDsl().delete(ORGANIZATIONS).where(ORGANIZATIONS.SHERDOGURL.eq(id)).execute() == 1;
    }


    /**
     * Get all the organizations that should appear in the event filter`
     *
     * @return
     */
    public List<MmathOrganization> getOrganizationsInEventFilter() {

        return getDsl().select().from(ORGANIZATIONS)
                .where(ORGANIZATIONS.SHERDOGURL.in(EVENT_FILTER_ORGANIZATIONS))
                .fetch(recordMapper);
    }

}
