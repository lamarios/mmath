package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Fighter;
import org.springframework.dao.DuplicateKeyException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by gz on 16-Sep-16.
 */
public class FighterProcessor extends Processor<MmathFighter> {
    private final MySQLDao dao;

    public FighterProcessor(Receiver receiver, MySQLDao dao, Sherdog sherdog) {
        super(receiver, sherdog);
        this.dao = dao;
    }

    @Override
    protected void propagate(MmathFighter obj) {
        obj.getFights().forEach(f -> {
//            fighterPool.convertAndSend(f.getFighter2().getSherdogUrl());
//            eventPool.convertAndSend(f.getEvent().getSherdogUrl());
            receiver.process(new ProcessItem(f.getFighter2().getSherdogUrl(), ProcessType.FIGHTER));
            receiver.process(new ProcessItem(f.getEvent().getSherdogUrl(), ProcessType.EVENT));
        });

    }

    @Override
    protected void insertToDao(MmathFighter obj) throws SQLException {
        try {
            dao.getFighterDAO().insert(obj);
        }catch (DuplicateKeyException e){
            logger.info("Fighter already exist, skipping insert");
        }
    }

    @Override
    protected void updateToDao(MmathFighter old, MmathFighter fromSherdog) throws SQLException {
        dao.getFighterDAO().update(fromSherdog);
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
        mmathFighter.setFights(fighter.getFights().stream().map(MmathFight::fromSherdog).collect(Collectors.toList()));

        return mmathFighter;
    }

    @Override
    protected LocalDateTime getLastUpdate(MmathFighter obj) {
        return obj.getLastUpdate();
    }

    @Override
    protected Optional<MmathFighter> getFromDao(String url) throws SQLException {
        MmathFighter mmathFighter = dao.getFighterDAO().getById(url);
        return Optional.ofNullable(mmathFighter);
    }
}
