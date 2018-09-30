package com.ftpix.mmath.model;

import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.codec.digest.DigestUtils;

public class AggregatedHypeTrain {
    private String fighter;
    @Expose
    private String name;
    @Expose
    private int count;


    @ExposeMethodResult("fighter")
    public String getMd5Fighter() {
        return DigestUtils.md5Hex(fighter);
    }

    public String getFighter() {
        return fighter;
    }

    public void setFighter(String fighter) {
        this.fighter = fighter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
