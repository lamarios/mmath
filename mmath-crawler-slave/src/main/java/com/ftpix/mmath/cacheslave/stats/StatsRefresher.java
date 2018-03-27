package com.ftpix.mmath.cacheslave.stats;

import com.ftpix.mmath.cacheslave.stats.implementations.GlassCannonStats;
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
        /*
        logger.info("Refreshing fighter search rank");
        dao.getFighterDAO().setAllFighterSearchRank();
        logger.info("Fighter search rank updated");

*/

        MostFightStats mostFightStats = new MostFightStats(dao);
        mostFightStats.process();

        GlassCannonStats glassCannonStats = new GlassCannonStats(dao);
        glassCannonStats.process();

        LongestCareer longestCareer = new LongestCareer(dao);
        longestCareer.process();
        new KoWinsStats(dao).process();
        new DecisionWinsStats(dao).process();
        new SubmissionWinsStats(dao).process();

    }


}
