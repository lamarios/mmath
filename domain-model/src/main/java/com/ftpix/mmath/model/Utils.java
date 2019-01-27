package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.Sherdog;

public class Utils {
    public static String cleanUrl(String url){
        return url.replace(Sherdog.BASE_URL, "");
    }
}
