package com.ftpix.mmath.parsers;

import com.google.gson.Gson;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.utils.GsonUtils;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gz on 16-Sep-16.
 */
public class FighterParser {

    private final static String NICKNAME = "nickname", HEIGHT = "height", WEIGHT = "weight", BIRTHDAY="birthday",
            WINS = "wins", LOSSES = "losses", DRAWS = "draws", NC = "nc", PICTURE = "picture",
            FIGHTS = "fights", LAST_UPDATE = "lastUpdate", LAST_COUNT_UPDATE = "lastCountUpdate",
            BETTER_THAN = "bettherThan", WEAKER_THAN = "weakerThan";
    private final  static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private final static Gson gson = GsonUtils.getGson();

    public static Document serialize(MmathFighter fighter){
        Document doc = SherdogObjectParser.serialize(fighter)
                .append(NICKNAME, fighter.getNickname())
                .append(HEIGHT, fighter.getHeight())
                .append(WEIGHT, fighter.getWeight())
                .append(WINS, fighter.getWins())
                .append(LOSSES, fighter.getLosses())
                .append(DRAWS, fighter.getDraws())
                .append(NC, fighter.getNc())
                .append(PICTURE, fighter.getPicture())
                .append(LAST_UPDATE, fighter.getLastUpdate().toString())
                .append(LAST_COUNT_UPDATE, fighter.getLastCountUpdate().toString())
                .append(BETTER_THAN, Long.toString(fighter.getBetterThan()))
                .append(WEAKER_THAN, Long.toString(fighter.getWeakerThan()));

        if(fighter.getBirthday() != null){
            doc = doc.append(BIRTHDAY, df.format(fighter.getBirthday()));

        }

        List<Document> fightDocs = fighter.getFights()
                .stream()
                .map(f -> FightParser.serialize(f))
                .collect(Collectors.toList());

        doc.append(FIGHTS, fightDocs);


        return doc;
    }


    public static MmathFighter parse(Document doc){
        return gson.fromJson(doc.toJson(), MmathFighter.class);
    }
}
