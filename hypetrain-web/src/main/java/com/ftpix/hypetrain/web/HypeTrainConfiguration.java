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
@Import({DaoConfiguration.class})
public class HypeTrainConfiguration {

    private Logger logger = LogManager.getLogger();
    @Value("${HYPETRAIN_PORT:15679}")
    private int port;

    public static boolean DEV_MODE = System.getProperty("dev", "false").equalsIgnoreCase("true");

    @Value("${HYPETRAIN_JWT_SALT:somesupersalt}")
    private String jwtSalt;

    @Value("${REDDIT_CLIENT_ID}")
    private String redditClientId;


    @Value("${REDDIT_SECRET}")
    private String redditSecret;



    @Value("${REDDIT_REDIRECT_URL:http://localhost:15679}")
    private String redditRedirectUrl;

    @Bean
    HypeTrainController hypeTrainController(MySQLDao dao) {
        return new HypeTrainController(dao, redditClientId, redditSecret, jwtSalt, redditRedirectUrl+"/post-login");
    }

    @Bean
    HypeTrainWeb hypeTrainWeb(HypeTrainController hypeTrainController) {
        Spark.port(port);

        if (DEV_MODE) {
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
