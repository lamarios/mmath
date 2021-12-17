package com.ftpix.mmath.cron.hypetrain;

import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.mysql.EventDAO;
import com.ftpix.mmath.dao.mysql.FightDAO;
import com.ftpix.mmath.dao.mysql.FighterDAO;
import com.ftpix.mmath.dao.mysql.HypeTrainDAO;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.FightType;
import net.dean.jraw.RedditClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

//@Component
public class HypeTrainNotifications {
    protected Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private HypeTrainDAO hypeTrainDAO;

    @Autowired
    private FighterDAO fighterDAO;


    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private EventDAO eventDAO;


    @Autowired
    private RedditClient redditClient;

    private final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM YYYY");


    //    @Scheduled(cron = "00 30 * * * ?")
    @Scheduled(fixedDelay = 60000, initialDelay = 0)
    public void sendNotifications() {
        BatchProcessor.forClass(String.class, 100)
                .withSupplier((batch, batchSize, offset) -> hypeTrainDAO.getAllUsers(offset, batch))
                .withProcessor(users -> {
                    users.forEach(this::processUser);

                }).start();
    }


    private void processUser(String user) {
        logger.info("Checking notifications for user " + user);


        final Map<MmathFighter, MmathFight> toNotifiy = new HashMap<>();

        hypeTrainDAO.getByUser(user)
                .stream()
                .forEach(t -> {

                    MmathFighter fighter = fighterDAO.getById(t.getFighterId());

                    fightDAO.getByFighter(fighter.getSherdogUrl())
                            .stream()
                            .filter(f -> f.getFightType() == FightType.UPCOMING)
                            .min(Comparator.comparing(MmathFight::getDate))
                            .ifPresent(f -> {
                                long days = Math.abs(ChronoUnit.DAYS.between(LocalDateTime.now(), f.getDate()));

                                if (t.getNextFight() == null || f.getId() != t.getNextFight()) {
                                    t.setNextFight(f.getId());
                                    t.setNotified(false);
                                }


                                if (!t.isNotified() && days <= 200) {
                                    toNotifiy.put(fighter, f);
//                                    t.setNotified(true);
                                }
                                hypeTrainDAO.update(t);


                            });


                });

        if (toNotifiy.size() > 0) {
            StringBuilder message = new StringBuilder("Upcoming fights from the fighters you follow: \n\n\n");

            toNotifiy.forEach((fighter, fight) -> {
                MmathFighter f1 = fighterDAO.getById(fight.getFighter1().getSherdogUrl());
                MmathFighter f2 = fighterDAO.getById(fight.getFighter2().getSherdogUrl());
                MmathEvent event = eventDAO.getById(fight.getEvent().getSherdogUrl());

                message.append("- " + f1.getName() + " vs " + f2.getName() + " -- " + event.getName() + " on " + fight.getDate().format(dateFormat) + "\n");
            });


            redditClient.me().inbox().compose(user, "MmaHypetrain: Incoming fights", message.toString());

            System.out.println(message.toString());
        }

    }
}
