package com.ftpix.mmath.cacheslave.receivers;

import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Fighter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class FighterReceiver extends Receiver<MmathFighter> {


    public FighterReceiver(RabbitTemplate fighterTemplate, RabbitTemplate orgTemplate, RabbitTemplate eventTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog) {
        super(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog);
    }

    @Override
    protected void propagate(MmathFighter obj) {
        obj.getFights().forEach(f -> {
            fighterTemplate.convertAndSend(f.getFighter2().getSherdogUrl());
            eventTemplate.convertAndSend(f.getEvent().getSherdogUrl());
        });
    }

    @Override
    protected void insertToDao(MmathFighter obj) {
        fighterDao.insert(obj);
    }

    @Override
    protected void updateToDao(MmathFighter old, MmathFighter fromSherdog) {
        fromSherdog.setLastUpdate(LocalDate.now());
        fromSherdog.setLastCountUpdate(old.getLastCountUpdate());
        fromSherdog.setBetterThan(old.getBetterThan());
        fromSherdog.setWeakerThan(old.getWeakerThan());

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
                    fighterTemplate.convertAndSend(f.getFighter2().getSherdogUrl());
                    eventTemplate.convertAndSend(f.getEvent().getSherdogUrl());
                });
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected MmathFighter getFromSherdog(String url) throws IOException, ParseException {
        Fighter fighter = sherdog.getFighter(url);
        return new MmathFighter(fighter);
    }

    @Override
    protected LocalDate getLastUpdate(MmathFighter obj) {
        return obj.getLastUpdate();
    }

    @Override
    protected Optional<MmathFighter> getFromDao(String url) {
        return fighterDao.getByUrl(url);
    }
}
