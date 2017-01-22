package com.ftpix.mmath.parsers;

import com.google.gson.Gson;

import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.utils.GsonUtils;

import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gz on 17-Sep-16.
 */
public class EventParser {

    private static final String ORGANIZATION = "organization", DATE = "date", FIGHTS = "fights", LOCATION = "location", LAST_UPDATE = "lastUpdate";
    private final static Gson gson = GsonUtils.getGson();



    public static Document serialize(MmathEvent event) {
        Document doc = SherdogObjectParser.serialize(event)
                .append(ORGANIZATION, SherdogObjectParser.serialize(event.getOrganization()))
                .append(DATE, event.getDate().toString())
                .append(LOCATION, event.getLocation())
                .append(LAST_UPDATE, event.getLastUpdate().toString());

        List<Document> fights = event.getFights()
                .stream()
                .map(f -> FightParser.serialize(f))
                .collect(Collectors.toList());

        doc = doc.append(FIGHTS, fights);


        return doc;
    }


    public static MmathEvent parse(Document doc) {
        return gson.fromJson(doc.toJson(), MmathEvent.class);
    }
}
