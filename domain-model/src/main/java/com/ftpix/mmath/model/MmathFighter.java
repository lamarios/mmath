package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.utils.HashUtils;

import java.time.LocalDate;

import io.gsonfire.annotations.ExposeMethodResult;

/**
 * Created by gz on 24-Sep-16.
 */
public class MmathFighter extends Fighter implements MmathModel {
    private LocalDate lastUpdate = LocalDate.now(), lastCountUpdate = LocalDate.now();
    private long betterThan = 0;
    private long weakerThan = 0;

    public MmathFighter() {
        super();
    }

    public MmathFighter(Fighter fighter){
        this.setSherdogUrl(fighter.getSherdogUrl());
        this.setFights(fighter.getFights());
        this.setName(fighter.getName());
        this.setPicture(fighter.getPicture());
        this.setBirthday(fighter.getBirthday());
        this.setDraws(fighter.getDraws());
        this.setLosses(fighter.getLosses());
        this.setWins(fighter.getWins());
        this.setWeight(fighter.getWeight());
        this.setHeight(fighter.getHeight());
        this.setNickname(fighter.getNickname());
        this.setNc(fighter.getNc());
    }

    @ExposeMethodResult("id")
    public String getId() {
        return HashUtils.hash(this.getSherdogUrl());
    }


    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalDate getLastCountUpdate() {
        return lastCountUpdate;
    }

    public void setLastCountUpdate(LocalDate lastCountUpdate) {
        this.lastCountUpdate = lastCountUpdate;
    }

    public long getBetterThan() {
        return betterThan;
    }

    public void setBetterThan(long betterThan) {
        this.betterThan = betterThan;
    }

    public long getWeakerThan() {
        return weakerThan;
    }

    public void setWeakerThan(long weakerThan) {
        this.weakerThan = weakerThan;
    }
}
