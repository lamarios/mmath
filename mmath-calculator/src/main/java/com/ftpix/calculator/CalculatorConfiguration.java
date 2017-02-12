package com.ftpix.calculator;

import com.ftpix.calculator.web.WebServer;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.caching.CachingConfiguration;
import com.ftpix.mmath.caching.FighterCache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@Import({DaoConfiguration.class, CachingConfiguration.class})
@PropertySource("classpath:config.properties")
public class CalculatorConfiguration {

    @Value("${calculator.port}")
    private int port;

    private final Logger logger = LogManager.getLogger();

    @Bean
    BetterThan betterThan(FighterCache fighterCache) {
        return new BetterThan(fighterCache);
    }

    @Bean
    WebServer webServer(BetterThan betterThan, FighterCache fighterCache) {
        WebServer server = new WebServer(betterThan, fighterCache, port);
        server.setupServer();

        return server;
    }

    /*@Bean
    CacheHandler cacheHandler(FighterDao fighterDao, Map<String, MmathFighter> fighterCache ) {
        CacheHandler cacheHandler = new CacheHandler(fighterDao, fighterCache);
        cacheHandler.refreshCache();
        return cacheHandler;
    }*/


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CalculatorConfiguration.class);

    }
}
