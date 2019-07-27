package com.ftpix.hypetrain.web;

import com.ftpix.mmath.DaoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DaoConfiguration.class})
public class HypeTrainConfiguration {

    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(HypeTrainConfiguration.class);
    }

}
