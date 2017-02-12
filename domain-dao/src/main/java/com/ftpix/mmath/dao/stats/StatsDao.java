package com.ftpix.mmath.dao.stats;

import com.ftpix.mmath.model.stats.Stats;
import com.ftpix.mmath.parsers.StatsParser;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import java.util.Optional;

/**
 * Created by gz on 12-Feb-17.
 */
public class StatsDao {
    private final MongoCollection<Document> collection;
    public StatsDao(MongoCollection<Document> fightStatsCollection) {
        collection = fightStatsCollection;
    }


    public void incrementCount(String id){

        Optional<Document> doc = Optional.ofNullable(collection.find(new Document("_id", id)).first());

        if(!doc.isPresent()){
            Stats stats = new Stats();
            stats.setId(id);
            stats.setCount(1);

            collection.insertOne(StatsParser.serialize(stats));
        }else{

            Stats stats = StatsParser.deserialize(doc.get());
            System.out.println("COUNT:"+stats.getId()+" -> "+stats.getCount());

            stats.setCount(stats.getCount()+1);

            collection.findOneAndReplace(new Document("_id", stats.getId()), StatsParser.serialize(stats));
        }
    }



}
