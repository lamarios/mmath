package com.ftpix.mmath.sherdog;

import com.ftpix.sherdogparser.Sherdog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 24-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
public class SherdogConfiguration {
    @Value("${sherdog.cache}")
    private String sherdogCache;


    ////////////////////
    //// Sherdog
    /////

    @Bean
    Sherdog sherdog() {
        return new Sherdog.Builder().withCacheFolder(sherdogCache).build();
    }

}
