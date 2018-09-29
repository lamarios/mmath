package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.Organization;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jms.core.JmsTemplate;

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


    public OrganizationProcessor(MySQLDao dao, JmsTemplate jmsTemplate, Sherdog sherdog, String fighterTopic, String eventTopic, String OrganizationTopic) {
        super(dao, jmsTemplate, sherdog, fighterTopic, eventTopic, OrganizationTopic);
    }

    @Override
    protected void propagate(MmathOrganization obj) {
        obj.getEvents().forEach(e -> {
            jmsTemplate.convertAndSend(eventTopic, e.getSherdogUrl());
        });
    }

    @Override
    protected void insertToDao(MmathOrganization obj) throws SQLException {
        try {
            dao.getOrganizationDAO().insert(obj);
        } catch (DuplicateKeyException e) {
            logger.info("Organization already exists, skipping insert");
        }
    }

    @Override
    protected void updateToDao(MmathOrganization old, MmathOrganization fromSherdog) {
        dao.getOrganizationDAO().update(fromSherdog);
    }

    @Override
    protected MmathOrganization getFromSherdog(String url) throws IOException, ParseException, SherdogParserException {
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

}
