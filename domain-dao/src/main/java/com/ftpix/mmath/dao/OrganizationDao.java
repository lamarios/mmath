package com.ftpix.mmath.dao;

import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.mmath.parsers.OrganizationParser;
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
public class OrganizationDao implements MmathDao<MmathOrganization> {


    private MongoCollection<Document> organizationCollection;
    private Logger logger = LogManager.getLogger();
    private Sherdog sherdog;


    public OrganizationDao(MongoCollection<Document> organizationCollection, Sherdog sherdog) {
        this.organizationCollection = organizationCollection;
        this.sherdog = sherdog;
    }

    public MmathOrganization insert(MmathOrganization f) {
        logger.info("Inserting fighter {}", f.getName());
        organizationCollection.insertOne(OrganizationParser.serialize(f));
        return f;
    }

    public MmathOrganization insert(String url) throws IOException, ParseException {
        logger.info("Inserting fighter via url: {}", url);
        MmathOrganization f = (MmathOrganization) sherdog.getOrganization(url);

        return insert(f);
    }


    public Optional<MmathOrganization> get(String id) {
        Document result = organizationCollection.find(new Document("_id", id)).first();

        if (result != null) {
            return Optional.of(OrganizationParser.parse(result));
        } else {
            return Optional.empty();
        }
    }


    public MmathOrganization update(MmathOrganization fighter) {
        return OrganizationParser.parse(organizationCollection.findOneAndReplace(new Document("_id", fighter.getId()), OrganizationParser.serialize(fighter)));
    }


    public Optional<MmathOrganization> getByUrl(String url) {
        Document result = organizationCollection.find(new Document("sherdogUrl", url)).first();

        if (result != null) {
            return Optional.of(OrganizationParser.parse(result));
        } else {
            return Optional.empty();
        }
    }

    public List<MmathOrganization> getAll() {
        List<MmathOrganization> fighters = new ArrayList<>();

        Block<Document> block = d -> {
            fighters.add(OrganizationParser.parse(d));
        };

        organizationCollection.find().forEach(block);

        logger.info("Found {} fighters", fighters.size());

        return fighters;
    }

    public Map<String, MmathOrganization> getAllAsMap() {
        Map<String, MmathOrganization> fighters = new HashMap<>();

        Block<Document> block = d -> {
            MmathOrganization f = OrganizationParser.parse(d);
            fighters.put(f.getSherdogUrl(), f);
        };

        organizationCollection.find().forEach(block);


        return fighters;
    }

    @Override
    public boolean delete(MmathOrganization object) {
        return delete(object.getId());
    }

    @Override
    public boolean delete(String id) {
        organizationCollection.findOneAndDelete(new Document("_id", id));

        return true;
    }

    @Override
    public boolean deleteByUrl(String url) {
        organizationCollection.findOneAndDelete(new Document("sherdogUrl", url));

        return true;
    }
}
