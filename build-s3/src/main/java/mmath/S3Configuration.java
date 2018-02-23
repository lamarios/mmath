package mmath;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties")
public class S3Configuration {

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

    @Bean
    public S3Helper s3Helper() {
        return new S3Helper(awsAccess, awsSecret, awsBucket, awsRegion, awsEndpoint);
    }
}
