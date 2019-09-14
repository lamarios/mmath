package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.webwatcher.interfaces.WebSite;
import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by gz on 24-Sep-16.
 */
public class MmathEvent implements WebSite {
    private String sherdogUrl;

    @Expose
    private ZonedDateTime date;

    @Expose
    private MmathOrganization organization;


    @Expose
    private List<MmathFight> fights;

    @Expose
    private String name;

    @Expose
    private String location;

    private LocalDateTime lastUpdate = LocalDateTime.now();
    private LocalDateTime lastContentCheck;
    private String contentHash;

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getSherdogUrl() {
        return sherdogUrl;
    }

    @ExposeMethodResult("id")
    public String getIdAsHash() {
        return DigestUtils.md5Hex(getSherdogUrl());
    }

    public static MmathEvent fromSherdog(Event event) {
        MmathEvent newEvent = new MmathEvent();
        newEvent.setDate(event.getDate());
        newEvent.setLocation(event.getLocation());
        newEvent.setName(event.getName());
        newEvent.setSherdogUrl(Utils.cleanUrl(event.getSherdogUrl()));

        MmathOrganization org = new MmathOrganization();
        org.setSherdogUrl(Utils.cleanUrl(event.getOrganization().getSherdogUrl()));

        newEvent.setOrganization(org);

        return newEvent;
    }


    @Override
    public String getUrl() {
        return Sherdog.BASE_URL + sherdogUrl;
    }

    @Override
    public LocalDateTime getLastCheck() {
        return lastContentCheck;
    }

    @Override
    public String getLastContentHash() {
        return contentHash;
    }

    @Override
    public String getCssSelector() {
        return ".fight_card .fight, .content .event_match";
    }

    @Override
    public void setUrl(String url) {
        throw new UnsupportedOperationException("Not supposed to set  URL");
    }

    @Override
    public void setLastCheck(LocalDateTime lastCheck) {
        lastContentCheck = lastCheck;
    }

    @Override
    public void setLastContentHash(String hash) {
        contentHash = hash;
    }

    @Override
    public void setCssSelector(String cssSelector) {
        throw new UnsupportedOperationException("Not supposed to set css selector");
    }
}
