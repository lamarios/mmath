package com.ftpix.mmath.dao.mysql;

import com.ftpix.mmath.model.MmathOrganization;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.List;

public class OrganizationDAO implements DAO<MmathOrganization, String> {

    private final JdbcTemplate template;

    private final RowMapper<MmathOrganization> rowMapper = (rs, i) -> {
        MmathOrganization o = new MmathOrganization();
        o.setSherdogUrl(rs.getString("sherdogUrl"));
        o.setLastUpdate(LocalDateTime.parse(rs.getString("lastUpdate"), DAO.TIME_FORMAT));
        o.setName(rs.getString("name"));
        return o;
    };

    public OrganizationDAO(JdbcTemplate template) {

        this.template = template;
    }

    @Override
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
        String query = "SELECT * FROM organizations WHERE sherdogUrl = ?";

        List<MmathOrganization> query1 = template.query(query, rowMapper, id);

        return query1.size() == 1 ? query1.get(0) : null;
    }

    @Override
    public List<MmathOrganization> getAll() {
        String query = "SELECT * FROM organizations";

        return template.query(query, rowMapper);
    }

    @Override
    public String insert(MmathOrganization o) {
        template.update("INSERT  INTO  organizations (sherdogUrl, lastUpdate, name) values (?, NOW(), ?)"
                , o.getSherdogUrl(), o.getName());
        return o.getSherdogUrl();
    }

    @Override
    public boolean update(MmathOrganization o) {
        return template.update("UPDATE organizations SET name = ?, lastUpdate = NOW() WHERE sherdogUrl = ?"
        , o.getName(), o.getSherdogUrl()) == 1;
    }

    @Override
    public boolean deleteById(String id) {
        return template.update("DELETE  FROM organizations WHERE sherdogUrl = ?", id) == 1;
    }


    /**
     *  Get all the organizations that should appear in the event filter`
     * @return
     */
    public List<MmathOrganization> getOrganizationsInEventFilter(){
        String query = "SELECT * FROM organizations WHERE sherdogUrl in ('https://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2','https://www.sherdog.com/organizations/Bellator-MMA-1960','https://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469','https://www.sherdog.com/organizations/One-Championship-3877')";

        return template.query(query, rowMapper);
    }

}
