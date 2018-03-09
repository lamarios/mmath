package com.ftpix.mmath.model.stats;

import com.ftpix.mmath.model.MmathFighter;

import java.time.LocalDateTime;

public class StatsEntry {

    private long id;
    private int percent;
    private MmathFighter figher;
    private StatsCategory category;
    private String textToShow, details;
    private LocalDateTime lastUpdate;


    public StatsCategory getCategory() {
        return category;
    }

    public void setCategory(StatsCategory category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public MmathFighter getFigher() {
        return figher;
    }

    public void setFigher(MmathFighter figher) {
        this.figher = figher;
    }

    public String getTextToShow() {
        return textToShow;
    }

    public void setTextToShow(String textToShow) {
        this.textToShow = textToShow;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
