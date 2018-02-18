package com.ftpix.mmath.model;

import com.ftpix.mmath.model.persisters.LocalDatePersister;
import com.ftpix.sherdogparser.models.Fighter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.naming.Name;

/**
 * Created by gz on 24-Sep-16.
 */
@DatabaseTable(tableName = "fighters")
public class MmathFighter {

    @DatabaseField(id = true, width = 1000)
    private String sherdogUrl;

    @DatabaseField()
    private Date lastUpdate = new Date();


    private List<MmathFight> fights;


    @DatabaseField
    private String name;
    @DatabaseField
    private String picture;
    @DatabaseField
    private Date birthday;
    @DatabaseField
    private int draws;
    @DatabaseField
    private int losses;
    @DatabaseField
    private int wins;
    @DatabaseField
    private String weight;
    @DatabaseField
    private String height;
    @DatabaseField
    private String nickname;
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

    public List<MmathFight> getFights() {
        return fights;
    }

    public void setFights(List<MmathFight> fights) {
        this.fights = fights;
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

}
