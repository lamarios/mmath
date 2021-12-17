package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    protected Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    protected JmsTemplate jmsTemplate;
    @Autowired
    protected Sherdog sherdog;
    @Autowired
    protected String fighterTopic;
    @Autowired
    protected String eventTopic;
    @Autowired
    protected String organizationTopic;

    public static int RATE = 1;


    @Override
    public void onMessage(Message message) {
        try {
            String msg = ((TextMessage) message).getText();
            this.process(msg.trim());
        } catch (JMSException e) {
            logger.error("Couldn't convert message to url");
        }
    }


    public void process(String id) {

        logger.info(this.getClass().getName()+" received: "+id);

        try {
            String fullUrl = Sherdog.BASE_URL + id;

            Optional<T> opt = getFromDao(id);

            Optional<T> toParse = Optional.empty();

            if (opt.isPresent()) {
                logger.info("["+id+"] already exists...");
                LocalDateTime now = LocalDateTime.now();
                T optResult = opt.get();

                LocalDateTime date = getLastUpdate(optResult);
                long daysbetween = ChronoUnit.DAYS.between(date, now);

                if (daysbetween >= RATE) {
                    logger.info("["+id+"] Info is too old, need to update");
                    T updated = getFromSherdog(fullUrl);


                    updateToDao(optResult, updated);

                    toParse = Optional.ofNullable(updated);

                }

            } else {
                logger.info("["+id+"] doesn't exist, need to get and insert");

                T obj = getFromSherdog(fullUrl);
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
