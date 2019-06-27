package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathOrganization;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


@Component
public class EventDAO extends DAO<MmathEvent, String> {


    @Autowired
    private JdbcTemplate template;


    private RowMapper<MmathEvent> rowMapper = (rs, i) -> {
        MmathEvent e = new MmathEvent();
        e.setSherdogUrl(rs.getString("sherdogUrl"));
        Optional.ofNullable(rs.getString("date")).ifPresent(s -> {
            e.setDate(ZonedDateTime.parse(s, DAO.TIME_FORMAT));
        });

        MmathOrganization org = new MmathOrganization();
        org.setSherdogUrl(rs.getString("organization_id"));
        e.setOrganization(org);

        e.setName(rs.getString("name"));
        e.setLocation(rs.getString("location"));
        e.setLastUpdate(LocalDateTime.parse(rs.getString("lastUpdate"), DAO.TIME_FORMAT));


        return e;
    };



    @Override
    @PostConstruct
    public void init() {


        String createTable = "CREATE TABLE IF NOT EXISTS events\n" +
                "(\n" +
                "  sherdogUrl      VARCHAR(1000) NOT NULL\n" +
                "    PRIMARY KEY,\n" +
                "  `date`            DATETIME      NULL,\n" +
                "  organization_id VARCHAR(1000) NULL,\n" +
                "  name            VARCHAR(255)  NULL,\n" +
                "  location        VARCHAR(255)  NULL,\n" +
                "  lastUpdate      DATETIME      NULL\n" +
                ")\n" +
                "  ENGINE = InnoDB;";

        template.execute(createTable);
    }

    @Override
    public MmathEvent getById(String id) {

        String query = "SELECT * FROM events WHERE sherdogUrl = ?";

        List<MmathEvent> query1 = template.query(query, rowMapper, id);

        return query1.size() == 1 ? query1.get(0) : null;
    }


    public MmathEvent getFromHash(String hash) {
        String query = "SELECT * FROM events WHERE MD5(sherdogUrl) = ?";

        List<MmathEvent> query1 = template.query(query, rowMapper, hash);

        return query1.size() == 1 ? query1.get(0) : null;
    }


    @Override
    public List<MmathEvent> getAll() {
        String query = "SELECT * FROM events";

        return template.query(query, rowMapper);
    }

    @Override
    public List<MmathEvent> getBatch(int offset, int limit) {
        String query = "SELECT * FROM events LIMI ?,?";

        return template.query(query, new Integer[]{offset, limit}, rowMapper);
    }

    public List<MmathEvent> getIncoming(String organizations, Integer page) {
        LocalDateTime now = LocalDateTime.now().minusDays(2);
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
        String orgsSQLIdentifier = "";
        if (orgsArray.length > 0) {
            orgsSQLIdentifier = new String(new char[orgsArray.length]).replace("\0", "?,");
            orgsSQLIdentifier = orgsSQLIdentifier.substring(0, orgsSQLIdentifier.length() - 1);
        }

        Object[] parameters = new Object[]{now.format(DAO.TIME_FORMAT)};

        if (orgsArray.length > 0) {
            parameters = ArrayUtils.addAll(parameters, orgsArray);
        }

        parameters = ArrayUtils.add(parameters, offset);
        parameters = ArrayUtils.add(parameters, limit);

        String query = "SELECT * FROM events WHERE `date` >= ? " + ((orgsArray.length > 0) ? "AND md5(organization_id) IN (" + orgsSQLIdentifier + ")" : "") + "ORDER BY `date`  LIMIT ?,?";

        return template.query(query, rowMapper, parameters);
    }


    public boolean deleteNotHappenedEvents() {
        return template.update("DELETE FROM events WHERE  `date`>= NOW()") >= 0;
    }

    @Override
    public String insert(MmathEvent e) {
        template.update("INSERT INTO events (sherdogUrl, `date`, organization_id, name, location, lastUpdate) VALUES (?,?,?,?,?,NOW())"
                , e.getSherdogUrl(), DAO.TIME_FORMAT.format(e.getDate()), e.getOrganization().getSherdogUrl(), e.getName(), e.getLocation());
        return e.getSherdogUrl();
    }

    @Override
    public boolean update(MmathEvent e) {
        return template.update("UPDATE events SET `date` = ?, organization_id=?, name=?, location = ?, lastUpdate = NOW() WHERE sherdogUrl = ?"
                , DAO.TIME_FORMAT.format(e.getDate()), e.getOrganization().getSherdogUrl(), e.getName(), e.getLocation(), e.getSherdogUrl()) == 1;
    }

    @Override
    public boolean deleteById(String id) {
        return template.update("DELETE FROM events WHERE  sherdogUrl = ?", id) == 1;
    }

}
