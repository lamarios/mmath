package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Fighter;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class FighterProcessor extends Processor<MmathFighter> {
    private final Dao<MmathFighter, String> fighterDao;

    public FighterProcessor(Receiver receiver, Dao<MmathFighter, String> fighterDao, Sherdog sherdog) {
        super(receiver, sherdog);
        this.fighterDao = fighterDao;
    }

    @Override
    protected void propagate(MmathFighter obj) {
        obj.getFightsAsFighter1().forEach(f -> {
//            fighterPool.convertAndSend(f.getFighter2().getSherdogUrl());
//            eventPool.convertAndSend(f.getEvent().getSherdogUrl());
            receiver.process(new ProcessItem(f.getFighter2().getSherdogUrl(), ProcessType.FIGHTER));
            receiver.process(new ProcessItem(f.getEvent().getSherdogUrl(), ProcessType.EVENT));
        });

        obj.getFightsAsFighter2().forEach(f -> {
            receiver.process(new ProcessItem(f.getFighter1().getSherdogUrl(), ProcessType.FIGHTER));
            receiver.process(new ProcessItem(f.getEvent().getSherdogUrl(), ProcessType.EVENT));

        });
    }

    @Override
    protected void insertToDao(MmathFighter obj) throws SQLException {
        fighterDao.createOrUpdate(obj);
    }

    @Override
    protected void updateToDao(MmathFighter old, MmathFighter fromSherdog) throws SQLException {
        fromSherdog.setLastUpdate(new Date());

        fighterDao.update(fromSherdog);
    }

    /* public void receiveMessage(String message) {
        logger.info("Fighter receiver:{}", message);

        try {


            Optional<MmathFighter> optFighter = fighterDao.getByUrl(message);

            Optional<MmathFighter> toParse = null;

            if (optFighter.isPresent()) {
                logger.info("[{}] Fighter already exists...", message);
                LocalDate now = LocalDate.now();
                MmathFighter fighter = optFighter.get();

                long daysbetween = ChronoUnit.DAYS.between(fighter.getLastUpdate(), now);

                if (daysbetween >= 5) {
                    logger.info("[{}] Info is too old, need to update", message);
                    MmathFighter updated = getFromSherdog(message);
                    updated.setLastUpdate(now);
                    updated.setLastCountUpdate(fighter.getLastCountUpdate());
                    updated.setBetterThan(fighter.getBetterThan());
                    updated.setWeakerThan(fighter.getWeakerThan());

                    fighterDao.update(updated);
                    toParse = Optional.of(updated);
                }

            } else {
                logger.info("[{}] doesn't exist, need to update", message);

                MmathFighter fighter = getFromSherdog(message);
                fighterDao.insert(fighter);
                toParse = Optional.of(fighter);
            }


            toParse.ifPresent(mmathFighter -> {
                mmathFighter.getFights().forEach(f -> {
                    fighterPool.convertAndSend(f.getFighter2().getSherdogUrl());
                    eventPool.convertAndSend(f.getEvent().getSherdogUrl());
                });
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected MmathFighter getFromSherdog(String url) throws IOException, ParseException {
        Fighter fighter = sherdog.getFighter(url);
        MmathFighter mmathFighter = MmathFighter.fromSherdong(fighter);

        return mmathFighter;
    }

    @Override
    protected Date getLastUpdate(MmathFighter obj) {
        return obj.getLastUpdate();
    }

    @Override
    protected Optional<MmathFighter> getFromDao(String url) throws SQLException {
        MmathFighter mmathFighter = fighterDao.queryForId(url);
        return Optional.ofNullable(mmathFighter);
    }
}
