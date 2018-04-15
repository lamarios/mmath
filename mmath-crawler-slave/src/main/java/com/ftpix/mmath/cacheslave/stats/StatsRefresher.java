package com.ftpix.mmath.cacheslave.stats;

import com.ftpix.mmath.cacheslave.stats.implementations.GlassCannonStats;
import com.ftpix.mmath.cacheslave.stats.implementations.HighestNcStats;
import com.ftpix.mmath.cacheslave.stats.implementations.LongestCareer;
import com.ftpix.mmath.cacheslave.stats.implementations.MostFightStats;
import com.ftpix.mmath.cacheslave.stats.implementations.winpercentage.DecisionWinsStats;
import com.ftpix.mmath.cacheslave.stats.implementations.winpercentage.KoWinsStats;
import com.ftpix.mmath.cacheslave.stats.implementations.winpercentage.SubmissionWinsStats;
import com.ftpix.mmath.dao.MySQLDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatsRefresher {

    private final MySQLDao dao;

    protected Logger logger = LogManager.getLogger();

    public StatsRefresher(MySQLDao dao) {
        this.dao = dao;
    }

    public void process() {

        new MostFightStats(dao).process();
        new GlassCannonStats(dao).process();
        new LongestCareer(dao).process();
        new KoWinsStats(dao).process();
        new DecisionWinsStats(dao).process();
        new SubmissionWinsStats(dao).process();
        new HighestNcStats(dao).process();


        logger.info("Refreshing fighter search rank");
        dao.getFighterDAO().setAllFighterSearchRank();
        logger.info("Fighter search rank updated");

    }


}
