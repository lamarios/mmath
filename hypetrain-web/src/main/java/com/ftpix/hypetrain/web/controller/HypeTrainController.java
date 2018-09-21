package com.ftpix.hypetrain.web.controller;

import com.ftpix.hypetrain.web.GsonTransformer;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sparknnotation.annotations.SparkController;
import com.ftpix.sparknnotation.annotations.SparkPost;
import com.ftpix.sparknnotation.annotations.SparkQueryParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


@SparkController
public class HypeTrainController {

    private Logger logger = LogManager.getLogger();

    private final MySQLDao dao;

    public HypeTrainController(MySQLDao dao) {
        this.dao = dao;
    }


    @SparkPost(value = "/api/search", transformer = GsonTransformer.class)
    public List<MmathFighter> index(@SparkQueryParam("name") String name) {

        List<MmathFighter> mmathFighters = dao.getFighterDAO().searchByName(name);
        return mmathFighters;
    }
}
