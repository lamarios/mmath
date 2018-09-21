package com.ftpix.hypetrain.web;

import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import spark.ResponseTransformer;

public class GsonTransformer implements ResponseTransformer {
    private Gson gson = GsonUtils.getGson();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
