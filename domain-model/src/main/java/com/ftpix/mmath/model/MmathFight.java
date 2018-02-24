package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.utils.DateUtils;
import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;


public class MmathFight {

    private long id;


    @Expose
    private MmathFighter fighter1;

    @Expose
    private MmathFighter fighter2;

    @Expose
    private MmathEvent event;

    @Expose
    private ZonedDateTime date;

    @Expose
    private FightResult result = FightResult.NOT_HAPPENED;

    @Expose
    private String winMethod;

    @Expose
    private String winTime;

    @Expose
    private int winRound;

    @Expose
    private LocalDateTime lastUpdate = LocalDateTime.now();

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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static MmathFight fromSherdog(Fight fight) {
        MmathFight newFight = new MmathFight();

        newFight.setDate(fight.getDate());

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
