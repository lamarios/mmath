package com.ftpix.mmath.cron.stats.implementations.winpercentage;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.stats.StatsCategory;

public class SubmissionWinsStats extends WinPercentageStats {
    public SubmissionWinsStats(MySQLDao dao) {
        super(dao, Condition.SUBMISSION);
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("SUB_WIN_PERCENT");
        cat.setDescription("Highest percentage of wins by submission for fighters with at least 10 wins");
        cat.setName("Gracie's blood");
        cat.setOrder(4);
        return cat;
    }
}
