package com.ftpix.mmath.web;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.j256.ormlite.dao.Dao;
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
@Import({DaoConfiguration.class,  S3Configuration.class})
public class WebConfiguration {

    @Value("${web.port}")
    private int port;



    @Bean
    WebServer server(Dao<MmathFighter, String> fighterDao, Dao<MmathFight, Long> fightDao, Dao<MmathEvent, String> eventDao, OrientDBDao orientDBDao, S3Helper s3Helper) {
        WebServer server = new WebServer(port, fighterDao, fightDao, eventDao, orientDBDao, s3Helper);

        server.startServer();

        return server;
    }


    public static void main(String... args){
        ApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfiguration.class);
    }
}
