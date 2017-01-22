package com.ftpix.mmath.parsers;

import com.google.gson.Gson;

import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.utils.GsonUtils;

import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gz on 17-Sep-16.
 */
public class OrganizationParser {
    private final static Gson gson = GsonUtils.getGson();

    public static Document serialize(MmathOrganization org){
        Document doc = SherdogObjectParser.serialize(org)
                .append("lastUpdate", org.getLastUpdate().toString());

        List<Document> events = org.getEvents()
                .stream()
                .map(e -> SherdogObjectParser.serialize(e))
                .collect(Collectors.toList());

        doc = doc.append("events", events);

        return doc;
    }



    public static MmathOrganization parse(Document doc){
        return gson.fromJson(doc.toJson(), MmathOrganization.class);
    }
}
