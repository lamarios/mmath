package com.ftpix.hypetrain.web;

import com.ftpix.hypetrain.web.controller.HypeTrainController;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import spark.Spark;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class})
public class HypeTrainConfiguration {

    private Logger logger = LogManager.getLogger();
    @Value("${hypetrain.port}")
    private int port;


    @Bean
    HypeTrainController hypeTrainController(MySQLDao dao) {
        return new HypeTrainController(dao);
    }

    @Bean
    HypeTrainWeb hypeTrainWeb(HypeTrainController hypeTrainController) {
        Spark.port(port);

        if (System.getProperty("dev", "false").equalsIgnoreCase("true")) {
            Path path = Paths.get(".").resolve("hypetrain-web/src/main/resources/web/public").toAbsolutePath();
//            path = Paths.get("/home/gz/IdeaProjects/mmath/mmath-web/src/main/resources/web/public");
            logger.info("DEV MODE {}", path.toString());
            Spark.externalStaticFileLocation(path.toString().replace("./", ""));

        } else {
            Spark.staticFiles.location("/web/public");
        }
        HypeTrainWeb hypeTrainWeb = new HypeTrainWeb(hypeTrainController);
        hypeTrainWeb.startApp();
        return hypeTrainWeb;
    }

    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(HypeTrainConfiguration.class);
    }

}
