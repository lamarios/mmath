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


    @Value("${aws.secret}")
    private String awsSecret;

    @Value("${aws.access}")
    private String awsAccess;

    @Value("${aws.bucket}")
    private String awsBucket;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.endpoint}")
private String awsEndpoint;
    ////////////////////
    //// Sherdog
    /////

    @Bean
    Sherdog sherdog() {
        return new Sherdog.Builder().withPictureProcessor(new PictureToS3(awsAccess, awsSecret, awsBucket, awsRegion, awsEndpoint)).build();
    }


}
