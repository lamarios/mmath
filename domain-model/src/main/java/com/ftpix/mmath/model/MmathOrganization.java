package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.Organization;
import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by gz on 24-Sep-16.
 */
public class MmathOrganization {
    private String sherdogUrl;

    private LocalDateTime lastUpdate = LocalDateTime.now();

    @Expose
    private List<MmathEvent> events;

    @Expose
    private String name;

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
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

    @ExposeMethodResult("id")
    public String getIdAsHash() {
        return DigestUtils.md5Hex(getSherdogUrl());
    }

    public static MmathOrganization fromSherdog(Organization o) {
        MmathOrganization org = new MmathOrganization();
        org.setSherdogUrl(o.getSherdogUrl());
        org.setName(o.getName());
        return org;
    }
}
