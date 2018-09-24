package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.cacheslave.Receiver;
import com.ftpix.mmath.cacheslave.Refresh;
import com.ftpix.mmath.cacheslave.models.ProcessItem;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.utils.DateUtils;
import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Created by gz on 16-Sep-16.
 */
public abstract class Processor<T> {

    protected Logger logger = LogManager.getLogger();
    protected final Receiver receiver;

    protected final Sherdog sherdog;


    private Gson gson = GsonUtils.getGson();


    public Processor(Receiver receiver, Sherdog sherdog) {
        this.sherdog = sherdog;
        this.receiver = receiver;
    }


    public void process(ProcessItem item) {

        logger.info("{} received:{}", this.getClass().getName(), item.getUrl());

        try {


            Optional<T> opt = getFromDao(item.getUrl());

            Optional<T> toParse = Optional.empty();

            if (opt.isPresent()) {
                logger.info("[{}] already exists...", item.getUrl());
                LocalDateTime now = LocalDateTime.now();
                T optResult = opt.get();

                LocalDateTime date = getLastUpdate(optResult);
                long daysbetween = ChronoUnit.DAYS.between(date, now);

                if (daysbetween >= Refresh.RATE) {
                    logger.info("[{}] Info is too old, need to update", item.getUrl());
                    T updated = getFromSherdog(item.getUrl());


                    updateToDao(optResult, updated);

                    toParse = Optional.ofNullable(updated);

                }

            } else {
                logger.info("[{}] doesn't exist, need to get and insert", item.getUrl());

                T obj = getFromSherdog(item.getUrl());
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
