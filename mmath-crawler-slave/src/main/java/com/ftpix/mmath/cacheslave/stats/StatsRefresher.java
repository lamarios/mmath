package com.ftpix.mmath.cacheslave.stats;

import com.ftpix.mmath.cacheslave.stats.implementations.GlassCannonStats;
import com.ftpix.mmath.cacheslave.stats.implementations.LongestCareer;
import com.ftpix.mmath.cacheslave.stats.implementations.MostFightStats;
import com.ftpix.mmath.dao.MySQLDao;

public class StatsRefresher {

    private final MySQLDao dao;


    public StatsRefresher(MySQLDao dao) {
        this.dao = dao;
    }

    public void process() {
        MostFightStats mostFightStats = new MostFightStats(dao);
        mostFightStats.process();

        GlassCannonStats glassCannonStats = new GlassCannonStats(dao);
        glassCannonStats.process();

        LongestCareer longestCareer = new LongestCareer(dao);
        longestCareer.process();

    }


}
