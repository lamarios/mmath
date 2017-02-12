package com.ftpix.mmath.parsers;

import com.ftpix.mmath.model.stats.Stats;
import com.ftpix.utils.GsonUtils;

import org.bson.Document;

/**
 * Created by gz on 12-Feb-17.
 */
public class StatsParser {
    private final static String ID = "_id", COUNT = "count";

    public static Document serialize(Stats stats) {
        Document doc = new Document(ID, stats.getId()).append(COUNT, stats.getCount());

        return doc;
    }

    public static Stats deserialize(Document doc) {
        Stats stats = GsonUtils.getGson().fromJson(doc.toJson(), Stats.class);
        stats.setId(doc.getString(ID));
        return stats;
    }
}
