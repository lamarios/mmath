package com.ftpix.mmath.sherdog;

import com.ftpix.sherdogparser.Sherdog;
import mmath.S3Configuration;
import mmath.S3Helper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 24-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import(S3Configuration.class)
public class SherdogConfiguration {



    ////////////////////
    //// Sherdog
    /////

    @Bean
    Sherdog sherdog(S3Helper s3Helper) {
        return new Sherdog.Builder().build();
    }


}
