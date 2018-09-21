package com.ftpix.mmath.model;

public class HypeTrain {
    private String user, fighterId;

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
}
