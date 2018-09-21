package com.ftpix.hypetrain.web;

import com.ftpix.hypetrain.web.controller.HypeTrainController;
import com.ftpix.sparknnotation.Sparknotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class HypeTrainWeb {

    private Logger logger = LogManager.getLogger();

    private final HypeTrainController hypeTrainController;

    public HypeTrainWeb(HypeTrainController hypeTrainController){
        this.hypeTrainController = hypeTrainController;
    }




    public void startApp(){
        logger.info("Starting Hype train webapp");


        try {
            Sparknotation.init(hypeTrainController);
        } catch (IOException e) {
            logger.error("Couldn't start server... bye",e);
            System.exit(1);
        }
    }

}
