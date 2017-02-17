package com.ftpix.mmath.cacheslave.models;

/**
 * Created by gz on 17-Feb-17.
 */
public class ProcessItem {

    private int retries = 0;
    private final String url;
    private final ProcessType type;

    public ProcessItem(String url, ProcessType type) {
        this.url = url;
        this.type=type;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getUrl() {
        return url;
    }

    public ProcessType getType() {
        return type;
    }
}
