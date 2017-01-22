package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.Organization;
import com.ftpix.utils.HashUtils;

import java.time.LocalDate;

import io.gsonfire.annotations.ExposeMethodResult;

/**
 * Created by gz on 24-Sep-16.
 */
public class MmathOrganization extends Organization implements MmathModel {

    private LocalDate lastUpdate = LocalDate.now();

    public MmathOrganization() {
    }

    public MmathOrganization(Organization org) {
        this.setEvents(org.getEvents());
        this.setName(org.getName());
        this.setSherdogUrl(org.getSherdogUrl());
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
