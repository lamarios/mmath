package com.ftpix.mmath.dao;

import com.ftpix.mmath.dao.mysql.EventDAO;
import com.ftpix.mmath.dao.mysql.FightDAO;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.dao.mysql.OrganizationDAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySQLDao {

    private final FighterDAO fighterDAO;
    private final EventDAO eventDAO;
    private final FightDAO fightDAO;
    private final OrganizationDAO organizationDAO;
    private JdbcTemplate template;

    public MySQLDao(JdbcTemplate template) {


        fightDAO = new FightDAO(template);
        eventDAO = new EventDAO(template);
        fighterDAO = new FighterDAO(template);
        organizationDAO = new OrganizationDAO(template);
        this.template = template;

        init();

    }

    private void init() {
        fighterDAO.init();
        fightDAO.init();
        eventDAO.init();
        organizationDAO.init();
    }

    public FighterDAO getFighterDAO() {
        return fighterDAO;
    }

    public EventDAO getEventDAO() {
        return eventDAO;
    }

    public FightDAO getFightDAO() {
        return fightDAO;
    }

    public OrganizationDAO getOrganizationDAO() {
        return organizationDAO;
    }
}
