package com.ftpix.mmath.web;

import com.ftpix.calculator.client.CalculatorClient;
import com.ftpix.calculator.client.CalculatorClientConfiguration;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.FighterDao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 26-Sep-16.
 */

@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, CalculatorClientConfiguration.class})
public class WebConfiguration {

    @Value("${web.port}")
    private int port;


    @Value("${web.cache}")
    private String webCacheFolder;

    @Value("${web.crawler.cache}")
    private String crawlerCacheFolder;


    @Bean
    WebServer server(FighterDao fighterDao, CalculatorClient calculatorClient) {
        WebServer server = new WebServer(port, webCacheFolder, crawlerCacheFolder, calculatorClient, fighterDao);

        server.startServer();

        return server;
    }


    public static void main(String... args){
        ApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfiguration.class);
    }
}
