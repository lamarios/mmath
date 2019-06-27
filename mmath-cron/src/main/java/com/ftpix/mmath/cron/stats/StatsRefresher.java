package com.ftpix.mmath.cron.stats;

import com.ftpix.mmath.cron.stats.implementations.GlassCannonStats;
import com.ftpix.mmath.cron.stats.implementations.LongestCareer;
import com.ftpix.mmath.cron.stats.implementations.MostFightStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.DecisionWinsStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.KoWinsStats;
import com.ftpix.mmath.cron.stats.implementations.winpercentage.SubmissionWinsStats;
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



    @Scheduled(cron = "0 0 20 ? * TUE")
    public void process() {

//        new MostFightStats(dao).process();
//        new GlassCannonStats(dao).process();// to fix
//        new LongestCareer(dao).process();
        koWinsStats.process();
//        new DecisionWinsStats(dao).process();
//        new SubmissionWinsStats(dao).process();


        logger.info("Refreshing fighter search rank");
//        dao.getFighterDAO().setAllFighterSearchRank();
        logger.info("Fighter search rank updated");

    }


}
