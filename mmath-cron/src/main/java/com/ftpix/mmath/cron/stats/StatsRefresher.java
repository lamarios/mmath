package com.ftpix.mmath.cron.stats;

import com.ftpix.mmath.cron.search.SearchRank;
import com.ftpix.mmath.cron.stats.implementations.GlassCannonStats;
import com.ftpix.mmath.cron.stats.implementations.LongestCareer;
import com.ftpix.mmath.cron.stats.implementations.MostFightStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.DecisionWinsStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.KoWinsStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.SubmissionWinsStats;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class StatsRefresher {


    @Autowired
    private MostFightStats mostFightStats;

    @Autowired
    private GlassCannonStats glassCannonStats;

    @Autowired
    private LongestCareer longestCareer;

    @Autowired
    private KoWinsStats koWinsStats;

    @Autowired
    private DecisionWinsStats decisionWinsStats;

    @Autowired
    private SubmissionWinsStats submissionWinsStats;

    @Autowired
    private SearchRank searchRank;

    private Log logger = LogFactory.getLog(this.getClass());


    @Autowired
    private FighterDAO fighterDAO;

    @Scheduled(cron = "0 0 20 ? * TUE")
    public void process() {

        mostFightStats.process();
        glassCannonStats.process();
        longestCareer.process();
        koWinsStats.process();
        decisionWinsStats.process();
        submissionWinsStats.process();


        logger.info("Refreshing fighter search rank");
        searchRank.process();
        logger.info("Fighter search rank updated");

    }


}
