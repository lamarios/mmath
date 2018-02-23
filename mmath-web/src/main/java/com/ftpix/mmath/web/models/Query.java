package com.ftpix.mmath.web.models;

import com.google.gson.annotations.Expose;

/**
 * Created by gz on 26-Sep-16.
 */
public class Query {
    @Expose
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
