package com.ftpix.mmath.web;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.web.controllers.EventsController;
import com.ftpix.mmath.web.controllers.MmathController;
import com.ftpix.mmath.web.controllers.StatsController;
import mmath.S3Configuration;
import mmath.S3Helper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Created by gz on 26-Sep-16.
 */

@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, S3Configuration.class})
public class WebConfiguration {

    @Value("${web.port}")
    private int port;


    @Bean
    EventsController eventsController(MySQLDao dao, WebServer server) {

        EventsController controller = new EventsController(dao);
        controller.declareEndPoints();

        return controller;
    }

    @Bean
    MmathController mmathController(MySQLDao dao, OrientDBDao orientDBDao, WebServer server) {
        MmathController mmathController = new MmathController(dao, orientDBDao);
        mmathController.declareEndPoints();

        return mmathController;
    }


    @Bean
    StatsController statsController(MySQLDao dao,  WebServer server){
        StatsController statsController = new StatsController(dao);
        statsController.declareEndPoints();

        return statsController;
    }

    @Bean
    WebServer server(S3Helper s3Helper) {
        WebServer server = new WebServer(port, s3Helper);

        server.startServer();

        return server;
    }


    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfiguration.class);
    }
}
