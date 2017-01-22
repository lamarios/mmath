package com.ftpix.mmath.cacheslave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gz on 17-Sep-16.
 */
public class ShutdownTimer {
    private static Logger logger = LogManager.getLogger();

    private static Timer timer;
    private static final int time = 1000 * 60 * 2;

    public static void start(){
        logger.info("reseting timer");
        stop();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Shutting down because of timer");
                System.exit(1);
            }
        }, time);

    }

    public static void stop(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
