package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public abstract class Processor<T> implements MessageListener {

    protected Logger logger = LogManager.getLogger();

    protected final JmsTemplate jmsTemplate;
    protected final Sherdog sherdog;
    protected final String fighterTopic;
    protected final String eventTopic;
    protected final String organizationTopic;
    protected final MySQLDao dao;


    @Override
    public void onMessage(Message message) {
        try {
            String msg = ((TextMessage) message).getText();
            this.process(msg);
        } catch (JMSException e) {
            logger.error("Couldn't convert message to url");
        }
    }

    public Processor(MySQLDao dao, JmsTemplate jmsTemplate, Sherdog sherdog, String fighterTopic, String eventTopic, String OrganizationTopic) {
        this.dao = dao;
        this.jmsTemplate = jmsTemplate;
        this.sherdog = sherdog;
        this.fighterTopic = fighterTopic;
        this.eventTopic = eventTopic;
        organizationTopic = OrganizationTopic;
    }


    public void process(String url) {

        logger.info("{} received:{}", this.getClass().getName(), url);

        try {


            Optional<T> opt = getFromDao(url);

            Optional<T> toParse = Optional.empty();

            if (opt.isPresent()) {
                logger.info("[{}] already exists...", url);
                LocalDateTime now = LocalDateTime.now();
                T optResult = opt.get();

                LocalDateTime date = getLastUpdate(optResult);
                long daysbetween = ChronoUnit.DAYS.between(date, now);

                if (daysbetween >= 1) {
                    logger.info("[{}] Info is too old, need to update", url);
                    T updated = getFromSherdog(url);


                    updateToDao(optResult, updated);

                    toParse = Optional.ofNullable(updated);

                }

            } else {
                logger.info("[{}] doesn't exist, need to get and insert", url);

                T obj = getFromSherdog(url);
                insertToDao(obj);
                toParse = Optional.ofNullable(obj);
            }


            toParse.ifPresent(this::propagate);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract void propagate(T obj);

    protected abstract void insertToDao(T obj) throws SQLException;

    protected abstract void updateToDao(T old, T fromSherdog) throws SQLException;

    protected abstract T getFromSherdog(String url) throws IOException, ParseException, SherdogParserException;

    protected abstract LocalDateTime getLastUpdate(T obj);

    protected abstract Optional<T> getFromDao(String url) throws SQLException;

}
