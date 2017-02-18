package com.ftpix.mmath.dao;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.parsers.FighterParser;
import com.ftpix.sherdogparser.Sherdog;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by gz on 18-Sep-16.
 */
public class FighterDao implements MmathDao<MmathFighter> {

    private MongoCollection<Document> fighterCollection;
    private Logger logger = LogManager.getLogger();
    private Sherdog sherdog;


    public FighterDao(MongoCollection<Document> fighterCollection, Sherdog sherdog) {
        this.fighterCollection = fighterCollection;
        this.sherdog = sherdog;
    }

    public MmathFighter insert(MmathFighter f) {
        logger.info("Inserting fighter {}", f.getName());
        fighterCollection.insertOne(FighterParser.serialize(f));
        return f;
    }

    public MmathFighter insert(String url) throws IOException, ParseException {
        logger.info("Inserting fighter via url: {}", url);
        MmathFighter f = (MmathFighter) sherdog.getFighter(url);
        f.setLastUpdate(LocalDate.now());
        f.setLastCountUpdate(LocalDate.now());
        return insert(f);
    }


    public Optional<MmathFighter> get(String id) {
        Document result = fighterCollection.find(new Document("_id", id)).first();

        if (result != null) {
            return Optional.of(FighterParser.parse(result));
        } else {
            return Optional.empty();
        }
    }


    public MmathFighter update(MmathFighter fighter) {
        return FighterParser.parse(fighterCollection.findOneAndReplace(new Document("_id", fighter.getId()), FighterParser.serialize(fighter)));
    }


    public Optional<MmathFighter> getByUrl(String url) {
        Document result = fighterCollection.find(new Document("sherdogUrl", url)).first();

        if (result != null) {
            return Optional.of(FighterParser.parse(result));
        } else {
            return Optional.empty();
        }
    }

    public List<MmathFighter> getAll() {
        List<MmathFighter> fighters = new ArrayList<>();

        Block<Document> block = d -> {
            fighters.add(FighterParser.parse(d));
        };

        fighterCollection.find().forEach(block);

        logger.info("Found {} fighters", fighters.size());

        return fighters;
    }

    public Map<String, MmathFighter> getAllAsMap() {
        Map<String, MmathFighter> fighters = new HashMap<>();

        Block<Document> block = d -> {
            MmathFighter f = FighterParser.parse(d);
            fighters.put(f.getId(), f);
        };

        fighterCollection.find().forEach(block);


        return fighters;
    }

    @Override
    public boolean delete(MmathFighter object) {
        return delete(object.getId());
    }

    @Override
    public boolean delete(String id) {
        fighterCollection.findOneAndDelete(new Document("_id", id));

        return true;
    }

    @Override
    public boolean deleteByUrl(String url) {
        fighterCollection.findOneAndDelete(new Document("sherdogUrl", url));

        return true;
    }


    public List<MmathFighter> findByName(String query, int limit){
        BasicDBObject q = new BasicDBObject();
        q.put("name",  Pattern.compile(query, Pattern.CASE_INSENSITIVE));


        List<MmathFighter> fighters = new ArrayList<>();

        Block<Document> block = d -> {
            fighters.add(FighterParser.parse(d));
        };

        fighterCollection.find(q).limit(limit).forEach(block);

        return fighters;
    }
}
