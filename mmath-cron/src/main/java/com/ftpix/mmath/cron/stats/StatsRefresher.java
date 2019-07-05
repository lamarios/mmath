package com.ftpix.mmath.cron.stats;

import com.ftpix.mmath.cron.stats.implementations.GlassCannonStats;
import com.ftpix.mmath.cron.stats.implementations.LongestCareer;
import com.ftpix.mmath.cron.stats.implementations.MostFightStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.DecisionWinsStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.KoWinsStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.SubmissionWinsStats;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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


    protected Logger logger = LogManager.getLogger();


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
        fighterDAO.setAllFighterSearchRank();
        logger.info("Fighter search rank updated");

    }


}
