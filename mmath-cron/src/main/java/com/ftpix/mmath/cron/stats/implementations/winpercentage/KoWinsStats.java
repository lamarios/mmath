package com.ftpix.mmath.cron.stats.implementations.winpercentage;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.stats.StatsCategory;

public class KoWinsStats extends WinPercentageStats {
    public KoWinsStats(MySQLDao dao) {
        super(dao, Condition.KO);
    }


    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("KO_WIN_PERCENT");
        cat.setDescription("Highest percentage of wins by KO / TKO for fighters with at least 10 wins");
        cat.setName("Hands of stone");
        cat.setOrder(3);
        return cat;
    }
}
