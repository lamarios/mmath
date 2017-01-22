package com.ftpix.calculator;

import com.ftpix.calculator.cache.CacheHandler;
import com.ftpix.calculator.web.WebServer;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@Import(DaoConfiguration.class)
@PropertySource("classpath:config.properties")
public class CalculatorConfiguration {

    @Value("${calculator.port}")
    private int port;

    private final Logger logger = LogManager.getLogger();

    @Bean
    BetterThan betterThan(Map<String, MmathFighter> fighterCache) {

        return new BetterThan(fighterCache);
    }

    @Bean
    BetterWeakerThanCount betterWeakerThanCount(Map<String, MmathFighter> fighterCache) {
        return new BetterWeakerThanCount(fighterCache);
    }

    @Bean
    Map<String, MmathFighter> fighterCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    WebServer webServer(BetterWeakerThanCount betterWeakerThanCount, BetterThan betterThan, FighterDao fighterDao) {
        WebServer server = new WebServer(betterThan, betterWeakerThanCount, fighterDao, port);
        server.setupServer();

        return server;
    }

    @Bean
    CacheHandler cacheHandler(FighterDao fighterDao, Map<String, MmathFighter> fighterCache ) {
        CacheHandler cacheHandler = new CacheHandler(fighterDao, fighterCache);
        cacheHandler.refreshCache();
        return cacheHandler;
    }


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CalculatorConfiguration.class);

    }
}
