package com.ftpix.hypetrain.web;

import com.ftpix.hypetrain.web.controller.HypeTrainController;
import com.ftpix.sparknnotation.Sparknotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import spark.Spark;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class HypeTrainWeb {

    private Logger logger = LogManager.getLogger();

    @Value("${HYPETRAIN_PORT:15679}")
    private int port;

    public static boolean DEV_MODE = System.getProperty("dev", "false").equalsIgnoreCase("true");

    @Autowired
    private HypeTrainController hypeTrainController;


    @PostConstruct
    public void startApp() {
        logger.info("Starting Hype train webapp");

        Spark.port(port);

        if (DEV_MODE) {
            Path path = Paths.get(".").resolve("hypetrain-web/src/main/resources/web/public").toAbsolutePath();
//            path = Paths.get("/home/gz/IdeaProjects/mmath/mmath-web/src/main/resources/web/public");
            logger.info("DEV MODE {}", path.toString());
            Spark.externalStaticFileLocation(path.toString().replace("./", ""));

        } else {
            Spark.staticFiles.location("/web/public");
        }

        try {
            Sparknotation.init(hypeTrainController);
        } catch (IOException e) {
            logger.error("Couldn't start server... bye", e);
            System.exit(1);
        }
    }

}
