package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gz on 24-Sep-16.
 */
@DatabaseTable(tableName = "fighters")
public class MmathFighter {

    @DatabaseField(id = true, width = 1000)
    private String sherdogUrl;

    @DatabaseField()
    private Date lastUpdate = new Date();


    @ForeignCollectionField(columnName = "fighter1_id")
    private ForeignCollection<MmathFight> fightsAsFighter1;


    @ForeignCollectionField(columnName = "fighter2_id")
    private ForeignCollection<MmathFight> fightsAsFighter2;


    public List<GsonFriendlyFight> gsonFriendlyFights;

    @Expose
    @DatabaseField
    private String name;
    @Expose
    @DatabaseField
    private String picture;
    @Expose
    @DatabaseField
    private Date birthday;
    @Expose
    @DatabaseField
    private int draws;
    @Expose
    @DatabaseField
    private int losses;
    @Expose
    @DatabaseField
    private int wins;
    @Expose
    @DatabaseField
    private String weight;
    @Expose
    @DatabaseField
    private String height;
    @Expose
    @DatabaseField
    private String nickname;
    @Expose
    @DatabaseField
    private int nc;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public void setSherdogUrl(String sherdogUrl) {
        this.sherdogUrl = sherdogUrl;
    }

    public String getSherdogUrl() {
        return sherdogUrl;
    }


    public String getPicture() {
        return picture;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getDraws() {
        return draws;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight() {
        return height;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

    public int getNc() {
        return nc;
    }

    @ExposeMethodResult("id")
    public String getIdAsHash() {
        return DigestUtils.md5Hex(getSherdogUrl());
    }

    public static MmathFighter fromSherdong(Fighter f) {
        MmathFighter fighter = new MmathFighter();

        fighter.setSherdogUrl(f.getSherdogUrl());
        fighter.setBirthday(f.getBirthday());
        fighter.setDraws(f.getDraws());
        fighter.setLosses(f.getLosses());
        fighter.setWins(f.getWins());
        fighter.setWeight(f.getWeight());
        fighter.setHeight(f.getHeight());
        fighter.setName(f.getName());
        fighter.setNickname(f.getNickname());
        fighter.setNc(f.getNc());
        fighter.setPicture(f.getPicture());

        return fighter;
    }

    public ForeignCollection<MmathFight> getFightsAsFighter1() {
        return fightsAsFighter1;
    }

    public void setFightsAsFighter1(ForeignCollection<MmathFight> fightsAsFighter1) {
        this.fightsAsFighter1 = fightsAsFighter1;
    }

    public ForeignCollection<MmathFight> getFightsAsFighter2() {
        return fightsAsFighter2;
    }

    public void setFightsAsFighter2(ForeignCollection<MmathFight> fightsAsFighter2) {
        this.fightsAsFighter2 = fightsAsFighter2;
    }

    @ExposeMethodResult("fights")
    public List<GsonFriendlyFight> getGsonFriendlyFights() throws SQLException {
getFightsAsFighter2().refreshCollection();
getFightsAsFighter1().refreshCollection();
        System.out.println(getFightsAsFighter1().size());
        System.out.println(getFightsAsFighter2().size());

        return getFightsAsFighter1().stream()
                .map(f -> {
                    GsonFriendlyFight gsonFight = new GsonFriendlyFight();
                    gsonFight.setDate(f.getEvent().getDate());
                    //we need to swap
                    if (f.getFighter2().getSherdogUrl().equalsIgnoreCase(getSherdogUrl())) {
                        f.setFighter2(f.getFighter1());
                        f.setFighter1(this);
                        switch (f.getResult()) {
                            case FIGHTER_1_WIN:
                                f.setResult(FightResult.FIGHTER_2_WIN);
                                break;
                            case FIGHTER_2_WIN:
                                f.setResult(FightResult.FIGHTER_1_WIN);
                                break;
                        }
                    }

                    gsonFight.setResult(f.getResult());
                    gsonFight.setOpponent(f.getFighter2().getName());
                    gsonFight.setEvent(f.getEvent().getName());
                    gsonFight.setWinMethod(f.getWinMethod());
                    gsonFight.setWinRound(f.getWinRound());
                    gsonFight.setWinTime(f.getWinTime());

                    return gsonFight;
                })
                .sorted(Comparator.comparing(f -> f.getDate()))
                .collect(Collectors.toList());
    }

    public void setGsonFriendlyFights(List<GsonFriendlyFight> gsonFriendlyFights) {
        this.gsonFriendlyFights = gsonFriendlyFights;
    }

}
