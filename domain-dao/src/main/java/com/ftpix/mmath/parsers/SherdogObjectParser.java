package com.ftpix.mmath.parsers;

import com.google.gson.Gson;

import com.ftpix.mmath.model.MmathModel;
import com.ftpix.sherdogparser.models.SherdogBaseObject;
import com.ftpix.utils.GsonUtils;

import org.bson.Document;

/**
 * Created by gz on 16-Sep-16.
 */
public class SherdogObjectParser {
    private static final String NAME ="name", SHERDOG_URL = "sherdogUrl";
    private final static Gson gson = GsonUtils.getGson();

    public static Document serialize(SherdogBaseObject object){
        return new Document(NAME, object.getName()).append(SHERDOG_URL, object.getSherdogUrl()).append("_id", MmathModel.generateId(object));
    }

    public static SherdogBaseObject parse(Document doc){
        return gson.fromJson(doc.toJson(), SherdogBaseObject.class);
    }


}
