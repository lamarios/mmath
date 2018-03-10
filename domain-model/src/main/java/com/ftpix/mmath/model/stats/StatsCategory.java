package com.ftpix.mmath.model.stats;

import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;

import java.time.LocalDateTime;

public class StatsCategory {

    @Expose
    private String id;
    @Expose
    private String name, description;

    private LocalDateTime lastUpdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @ExposeMethodResult("lastUpdate")
    public String formattedDate(){
        if(lastUpdate != null) {
            return lastUpdate.toLocalDate().toString();
        }else{
            return "";
        }
    }
}
