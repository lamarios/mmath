package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.sherdogparser.Sherdog;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class OrganizationProcessor extends Processor<MmathOrganization> {

private final OrganizationDao orgDao;
    public OrganizationProcessor(Receiver receiver, OrganizationDao orgDao, Sherdog sherdog) {
        super(receiver, sherdog);
        this.orgDao = orgDao;
    }

    @Override
    protected void propagate(MmathOrganization obj) {
        obj.getEvents().forEach(e -> {
            //eventPool.convertAndSend(e.getSherdogUrl());
            receiver.process(new ProcessItem(e.getSherdogUrl(), ProcessType.EVENT));
        });
    }

    @Override
    protected void insertToDao(MmathOrganization obj) {
        orgDao.insert(obj);
    }

    @Override
    protected void updateToDao(MmathOrganization old, MmathOrganization fromSherdog) {
        fromSherdog.setLastUpdate(LocalDate.now());
    }

    @Override
    protected MmathOrganization getFromSherdog(String url) throws IOException, ParseException {
        return new MmathOrganization(sherdog.getOrganization(url));
    }

    @Override
    protected LocalDate getLastUpdate(MmathOrganization obj) {
        return obj.getLastUpdate();
    }

    @Override
    protected Optional<MmathOrganization> getFromDao(String url) {
        return orgDao.getByUrl(url);
    }

    /*public void receiveMessage(String message) {
        logger.info("Organization receiver:{}", message);

        try {

            Optional<MmathOrganization> optOrg = orgDao.getByUrl(message);

            Optional<MmathOrganization> toParse = null;

            if (optOrg.isPresent()) {
                logger.info("[{}] Organization already exists...", message);
                LocalDate now = LocalDate.now();
                MmathFighter fighter = optFighter.get();

                long daysbetween = ChronoUnit.DAYS.between(fighter.getLastUpdate(), now);

                if(daysbetween >= 5){
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


        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/
}
