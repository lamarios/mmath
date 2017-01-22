package com.ftpix.mmath.parsers;

import com.google.gson.Gson;

import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.utils.GsonUtils;

import org.bson.Document;

/**
 * Created by gz on 16-Sep-16.
 */
public class FightParser {

    private final static String EVENT = "event", FIGHTER_1 = "fighter1", FIGHTER_2 = "fighter2", DATE = "date", RESULT = "result", WIN_METHOD = "winMethod", WIN_TIME = "winTime", WIN_ROUND = "winRound";
    private final static Gson gson = GsonUtils.getGson();

    public static Document serialize(Fight fight){
        return new Document(EVENT, SherdogObjectParser.serialize(fight.getEvent()))
                .append(FIGHTER_1, SherdogObjectParser.serialize(fight.getFighter1()))
                .append(FIGHTER_2, SherdogObjectParser.serialize(fight.getFighter2()))
                .append(DATE,fight.getDate().toString())
                .append(RESULT, fight.getResult().name())
                .append(WIN_METHOD, fight.getWinMethod())
                .append(WIN_TIME, fight.getWinTime())
                .append(WIN_ROUND, fight.getWinRound());
    }


    public static Fight parse(Document doc){
        return gson.fromJson(doc.toJson(), Fight.class);
    }
}
