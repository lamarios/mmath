package com.ftpix.mmath.model;

import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.codec.digest.DigestUtils;

public class HypeTrain {
    @Expose
    private String user;
    private String fighterId;

    private boolean notified;
    private Long nextFight;

    @Expose
    private String fighterName;

    public HypeTrain(String user, String fighterId) {
        this.user = user;
        this.fighterId = fighterId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFighterId() {
        return fighterId;
    }

    public void setFighterId(String fighterId) {
        this.fighterId = fighterId;
    }


    public String getFighterName() {
        return fighterName;
    }

    public void setFighterName(String fighterName) {
        this.fighterName = fighterName;
    }

    @ExposeMethodResult("id")
    public String getIdAsHash() {
        return DigestUtils.md5Hex(fighterId);
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public Long getNextFight() {
        return nextFight;
    }

    public void setNextFight(Long nextFight) {
        this.nextFight = nextFight;
    }
}
