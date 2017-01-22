package com.ftpix.mmath.dao;

import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.parsers.EventParser;
import com.ftpix.sherdogparser.Sherdog;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by gz on 25-Sep-16.
 */
public class EventDao implements MmathDao<MmathEvent> {

    private MongoCollection<Document> eventCollection;
    private Logger logger = LogManager.getLogger();
    private Sherdog sherdog;


    public EventDao(MongoCollection<Document> eventCollection, Sherdog sherdog) {
        this.eventCollection = eventCollection;
        this.sherdog = sherdog;
    }

    public MmathEvent insert(MmathEvent f) {
        logger.info("Inserting fighter {}", f.getName());
        eventCollection.insertOne(EventParser.serialize(f));
        return f;
    }

    public MmathEvent insert(String url) throws IOException, ParseException {
        logger.info("Inserting fighter via url: {}", url);
        MmathEvent f = (MmathEvent) sherdog.getEvent(url);

        return insert(f);
    }


    public Optional<MmathEvent> get(String id) {
        Document result = eventCollection.find(new Document("_id", id)).first();

        if (result != null) {
            return Optional.of(EventParser.parse(result));
        } else {
            return Optional.empty();
        }
    }


    public MmathEvent update(MmathEvent fighter) {
        return EventParser.parse(eventCollection.findOneAndReplace(new Document("_id", fighter.getId()), EventParser.serialize(fighter)));
    }


    public Optional<MmathEvent> getByUrl(String url) {
        Document result = eventCollection.find(new Document("sherdogUrl", url)).first();

        if (result != null) {
            return Optional.of(EventParser.parse(result));
        } else {
            return Optional.empty();
        }
    }

    public List<MmathEvent> getAll() {
        List<MmathEvent> fighters = new ArrayList<>();

        Block<Document> block = d -> {
            fighters.add(EventParser.parse(d));
        };

        eventCollection.find().forEach(block);

        logger.info("Found {} fighters", fighters.size());

        return fighters;
    }

    public Map<String, MmathEvent> getAllAsMap() {
        Map<String, MmathEvent> fighters = new HashMap<>();

        Block<Document> block = d -> {
            MmathEvent f = EventParser.parse(d);
            fighters.put(f.getSherdogUrl(), f);
        };

        eventCollection.find().forEach(block);


        return fighters;
    }

    @Override
    public boolean delete(MmathEvent object) {
        return delete(object.getId());
    }

    @Override
    public boolean delete(String id) {
        eventCollection.findOneAndDelete(new Document("_id", id));

        return true;
    }

    @Override
    public boolean deleteByUrl(String url) {
        eventCollection.findOneAndDelete(new Document("sherdogUrl", url));

        return true;
    }
}
