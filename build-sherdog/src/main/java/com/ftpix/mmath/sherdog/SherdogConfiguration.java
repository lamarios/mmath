package com.ftpix.mmath.sherdog;

import com.ftpix.sherdogparser.Sherdog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 24-Sep-16.
 */
@Configuration
public class SherdogConfiguration {



    ////////////////////
    //// Sherdog
    /////

    @Bean
    Sherdog sherdog() {
        return new Sherdog.Builder().build();
    }


}
