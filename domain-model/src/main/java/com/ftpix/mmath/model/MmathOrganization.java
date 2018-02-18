package com.ftpix.mmath.model;

import com.ftpix.mmath.model.persisters.LocalDatePersister;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Organization;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.gsonfire.annotations.ExposeMethodResult;

/**
 * Created by gz on 24-Sep-16.
 */
@DatabaseTable(tableName = "organizations")
public class MmathOrganization {
    @DatabaseField(id = true, width = 1000)
    private String sherdogUrl;

    @DatabaseField()
    private Date lastUpdate = new Date();

    private List<MmathEvent> events;
    @DatabaseField
    private String name;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<MmathEvent> getEvents() {
        return events;
    }

    public void setEvents(List<MmathEvent> events) {
        this.events = events;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSherdogUrl(String sherdogUrl) {
        this.sherdogUrl = sherdogUrl;
    }

    public String getSherdogUrl() {
        return sherdogUrl;
    }


    public static MmathOrganization fromSherdog(Organization o) {
        MmathOrganization org = new MmathOrganization();
        org.setSherdogUrl(o.getSherdogUrl());
        org.setName(o.getName());
        return org;
    }
}
