package com.ftpix.mmath.cron.stats.implementations.winpercentage;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.stats.StatsCategory;

public class DecisionWinsStats extends WinPercentageStats {
    public DecisionWinsStats(MySQLDao dao) {
        super(dao, Condition.DECISION);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("DECISION_WIN_PERCENT");
        cat.setDescription("Highest percentage of wins by decision for fighters with at least 10 wins.");
        cat.setName("Judge's favorite");
        cat.setOrder(5);
        return cat;
    }
}
