package com.ftpix.mmath.web;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
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
    WebServer server(MySQLDao dao, OrientDBDao orientDBDao, S3Helper s3Helper) {
        WebServer server = new WebServer(port, dao, orientDBDao, s3Helper);

        server.startServer();

        return server;
    }


    public static void main(String... args){
        ApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfiguration.class);
    }
}
