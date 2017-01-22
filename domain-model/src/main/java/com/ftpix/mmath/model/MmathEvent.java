package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.Event;
import com.ftpix.utils.HashUtils;

import java.time.LocalDate;

import io.gsonfire.annotations.ExposeMethodResult;

/**
 * Created by gz on 24-Sep-16.
 */
public class MmathEvent extends Event implements MmathModel{
    private LocalDate lastUpdate = LocalDate.now();

    public MmathEvent(){}

    public MmathEvent(Event event){
        this.setDate(event.getDate());
        this.setOrganization(event.getOrganization());
        this.setFights(event.getFights());
        this.setName(event.getName());
        this.setSherdogUrl(event.getSherdogUrl());
        this.setLocation(event.getLocation());
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
}
