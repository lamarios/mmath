package com.ftpix.utils;

import com.google.gson.Gson;

import com.fatboyindustrial.gsonjavatime.Converters;

import io.gsonfire.GsonFireBuilder;

/**
 * Created by gz on 24-Sep-16.
 */
public class GsonUtils {
    public static Gson getGson() {
        return Converters.registerLocalDate(Converters.registerZonedDateTime(new GsonFireBuilder()
                .enableExposeMethodResult().createGsonBuilder())).create();
    }
}
