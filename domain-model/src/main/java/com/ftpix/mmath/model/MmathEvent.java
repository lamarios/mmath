package com.ftpix.mmath.model;

import com.ftpix.mmath.model.persisters.LocalDatePersister;
import com.ftpix.mmath.model.persisters.ZonedDateTimePersister;
import com.ftpix.sherdogparser.models.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import com.ftpix.utils.DateUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by gz on 24-Sep-16.
 */
@DatabaseTable(tableName = "events")
public class MmathEvent {
    @DatabaseField(id = true, width = 1000)
    private String sherdogUrl;


    @DatabaseField()
    private Date date;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1, width = 1000)
    private MmathOrganization organization;


    private List<MmathFight> fights;

    @DatabaseField
    private String name;

    @DatabaseField
    private String location;

    @DatabaseField()
    private Date lastUpdate = new Date();

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MmathOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(MmathOrganization organization) {
        this.organization = organization;
    }

    public List<MmathFight> getFights() {
        return fights;
    }

    public void setFights(List<MmathFight> fights) {
        this.fights = fights;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSherdogUrl(String sherdogUrl) {
        this.sherdogUrl = sherdogUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }


    public String getName() {
        return name;
    }

    public String getSherdogUrl() {
        return sherdogUrl;
    }

    public static MmathEvent fromSherdog(Event event) {
        MmathEvent newEvent = new MmathEvent();
        newEvent.setDate(DateUtils.fromZonedDateTime(event.getDate()));
        newEvent.setLocation(event.getLocation());
        newEvent.setName(event.getName());
        newEvent.setSherdogUrl(event.getSherdogUrl());

        MmathOrganization org = new MmathOrganization();
        org.setSherdogUrl(event.getOrganization().getSherdogUrl());

        newEvent.setOrganization(org);

        return newEvent;
    }


}
