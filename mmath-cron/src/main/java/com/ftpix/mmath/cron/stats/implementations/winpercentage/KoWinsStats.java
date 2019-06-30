package com.ftpix.mmath.cron.stats.implementations.winpercentage;

import com.ftpix.mmath.model.stats.StatsCategory;
import org.springframework.stereotype.Component;


@Component
public class KoWinsStats extends WinPercentageStats {

    public KoWinsStats() {
        condition = Condition.KO;
    }

    @Override
    protected StatsCategory getStatsCategory() {
        StatsCategory cat = new StatsCategory();
        cat.setId("KO_WIN_PERCENT");
        cat.setDescription("Highest number of wins wins by KO / TKO for fighters with at least 10 wins");
        cat.setName("Hands of stone");
        cat.setOrder(3);
        return cat;
    }
}
