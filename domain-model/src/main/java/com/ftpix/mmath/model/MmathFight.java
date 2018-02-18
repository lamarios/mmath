package com.ftpix.mmath.model;

import com.ftpix.mmath.model.persisters.ZonedDateTimePersister;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.utils.DateUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.ZonedDateTime;
import java.util.Date;


@DatabaseTable(tableName = "fights")
public class MmathFight {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private long id;


    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 0, uniqueCombo = true, width = 1000)
    private MmathFighter fighter1;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 0, uniqueCombo = true, width = 1000)
    private MmathFighter fighter2;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 0, uniqueCombo = true, width = 1000)
    private MmathEvent event;

    @DatabaseField()
    private Date date;

    @DatabaseField
    private FightResult result = FightResult.NOT_HAPPENED;
    @DatabaseField
    private String winMethod;
    @DatabaseField
    private String winTime;
    @DatabaseField
    private int winRound;

    @DatabaseField
    private Date lastUpdate = new Date();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MmathEvent getEvent() {
        return event;
    }

    public void setEvent(MmathEvent event) {
        this.event = event;
    }

    public MmathFighter getFighter1() {
        return fighter1;
    }

    public void setFighter1(MmathFighter fighter1) {
        this.fighter1 = fighter1;
    }

    public MmathFighter getFighter2() {
        return fighter2;
    }

    public void setFighter2(MmathFighter fighter2) {
        this.fighter2 = fighter2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FightResult getResult() {
        return result;
    }

    public void setResult(FightResult result) {
        this.result = result;
    }

    public String getWinMethod() {
        return winMethod;
    }

    public void setWinMethod(String winMethod) {
        this.winMethod = winMethod;
    }

    public String getWinTime() {
        return winTime;
    }

    public void setWinTime(String winTime) {
        this.winTime = winTime;
    }

    public int getWinRound() {
        return winRound;
    }

    public void setWinRound(int winRound) {
        this.winRound = winRound;
    }


    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static MmathFight fromSherdog(Fight fight) {
        MmathFight newFight = new MmathFight();

        newFight.setDate(DateUtils.fromZonedDateTime(fight.getDate()));

        MmathEvent event = new MmathEvent();
        event.setSherdogUrl(fight.getEvent().getSherdogUrl());
        newFight.setEvent(event);

        MmathFighter fighter1 = new MmathFighter(), fighter2 = new MmathFighter();

        fighter1.setSherdogUrl(fight.getFighter1().getSherdogUrl());
        fighter2.setSherdogUrl(fight.getFighter2().getSherdogUrl());

        newFight.setFighter1(fighter1);
        newFight.setFighter2(fighter2);

        newFight.setResult(fight.getResult());
        newFight.setWinMethod(fight.getWinMethod());
        newFight.setWinRound(fight.getWinRound());
        newFight.setWinTime(fight.getWinTime());


        return newFight;


    }
}
