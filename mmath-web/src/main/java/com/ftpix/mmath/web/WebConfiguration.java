package com.ftpix.mmath.web;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.web.controllers.EventsController;
import com.ftpix.mmath.web.controllers.MmathController;
import com.ftpix.mmath.web.controllers.StatsController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Created by gz on 26-Sep-16.
 */

@Configuration
@Import({DaoConfiguration.class})
public class WebConfiguration {

    @Value("${MMATH_PORT:15678}")
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
    WebServer server() {
        WebServer server = new WebServer(port);

        server.startServer();

        return server;
    }


    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfiguration.class);
    }
}
