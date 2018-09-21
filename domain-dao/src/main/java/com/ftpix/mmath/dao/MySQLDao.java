package com.ftpix.mmath.dao;

import com.ftpix.mmath.dao.mysql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySQLDao {

    private Logger logger = LogManager.getLogger();
    private final FighterDAO fighterDAO;
    private final EventDAO eventDAO;
    private final FightDAO fightDAO;
    private final OrganizationDAO organizationDAO;
    private final StatsCategoryDAO statsCategoryDAO;
    private final StatsEntryDAO statsEntryDAO;
    private final HypeTrainDAO hypeTrainDAO;
    private final JdbcTemplate template;

    public MySQLDao(JdbcTemplate template) {


        fightDAO = new FightDAO(template);
        eventDAO = new EventDAO(template);
        fighterDAO = new FighterDAO(template);
        organizationDAO = new OrganizationDAO(template);
        statsCategoryDAO = new StatsCategoryDAO(template);
        statsEntryDAO = new StatsEntryDAO(template);
        hypeTrainDAO = new HypeTrainDAO(template);

        this.template = template;

        init();

    }

    private void init() {
        try {
            fighterDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }

        try {
            fightDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }

        try {
            eventDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }

        try {
            organizationDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }


        try {
            statsCategoryDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }

        try {
            statsEntryDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }

        try {
            hypeTrainDAO.init();
        } catch (Exception e) {
            logger.warn("Init script failing, might be on purpose", e);
        }

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

    public StatsCategoryDAO getStatsCategoryDAO() {
        return statsCategoryDAO;
    }

    public HypeTrainDAO getHypeTrainDAO() {
        return hypeTrainDAO;
    }

    public StatsEntryDAO getStatsEntryDAO() {
        return statsEntryDAO;
    }
}
