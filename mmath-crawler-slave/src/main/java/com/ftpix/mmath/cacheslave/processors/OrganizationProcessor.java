package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.mmath.cacheslave.models.ProcessType;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Organization;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public class OrganizationProcessor extends Processor<MmathOrganization> {

    private final MySQLDao dao;

    public OrganizationProcessor(Receiver receiver, MySQLDao dao, Sherdog sherdog) {
        super(receiver, sherdog);
        this.dao = dao;
    }

    @Override
    protected void propagate(MmathOrganization obj) {
        obj.getEvents().forEach(e -> {
            receiver.process(new ProcessItem(e.getSherdogUrl(), ProcessType.EVENT));
        });
    }

    @Override
    protected void insertToDao(MmathOrganization obj) throws SQLException {
        dao.getOrganizationDAO().insert(obj);
    }

    @Override
    protected void updateToDao(MmathOrganization old, MmathOrganization fromSherdog) {
        dao.getOrganizationDAO().update(fromSherdog);
    }

    @Override
    protected MmathOrganization getFromSherdog(String url) throws IOException, ParseException {
        Organization organization = sherdog.getOrganization(url);
        MmathOrganization org = MmathOrganization.fromSherdog(organization);

        org.setEvents(new ArrayList<>());
        organization.getEvents().forEach(e -> org.getEvents().add(MmathEvent.fromSherdog(e)));


        return org;
    }

    @Override
    protected LocalDateTime getLastUpdate(MmathOrganization obj) {
        return obj.getLastUpdate();
    }

    @Override
    protected Optional<MmathOrganization> getFromDao(String url) throws SQLException {
        return Optional.ofNullable(dao.getOrganizationDAO().getById(url));
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
