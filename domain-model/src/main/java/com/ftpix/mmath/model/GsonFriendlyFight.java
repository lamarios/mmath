package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.FightType;
import com.google.gson.annotations.Expose;

import java.time.ZonedDateTime;
import java.util.Optional;

public class GsonFriendlyFight {

    public GsonFriendlyFight(MmathFight f){

        setDate(f.getEvent().getDate());
        //we need to swap

        setResult(f.getResult());
        Optional<String> opponent = Optional.ofNullable(f.getFighter2()).map(MmathFighter::getName);
        if(opponent.isPresent()){
            setOpponent(f.getFighter2().getName());
        }else{
           setOpponent("Unknown Fighter");
        }

        setEventId(f.getEvent().getIdAsHash());

        setEvent(f.getEvent().getName());
        setWinMethod(f.getWinMethod());
        setWinRound(f.getWinRound());
        setWinTime(f.getWinTime());
        setType(f.getFightType());

    }


    @Expose
    private String eventId;

    @Expose
    private String opponent;

    @Expose
    private FightResult result;

    @Expose
    private ZonedDateTime date;

    @Expose
    private String event;

    @Expose
    private String winMethod;

    @Expose
    private String winTime;

    @Expose
    private FightType type;

    @Expose
    private int winRound;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public FightResult getResult() {
        return result;
    }

    public void setResult(FightResult result) {
        this.result = result;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public FightType getType() {
        return type;
    }

    public void setType(FightType type) {
        this.type = type;
    }
}
