package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.SherdogBaseObject;

import java.time.LocalDate;

/**
 * Created by gz on 25-Sep-16.
 */
public interface MmathModel {
    final static String SHERDOG_BASE_URL = "http://www.sherdog.com/";

    public LocalDate getLastUpdate();
    public String getId();

    public static String generateId(SherdogBaseObject object){
        String url = object.getSherdogUrl().replace(SHERDOG_BASE_URL, "").replaceAll("/", "-");

        return url;
    }
}
