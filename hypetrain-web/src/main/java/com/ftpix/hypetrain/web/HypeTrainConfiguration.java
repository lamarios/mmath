package com.ftpix.hypetrain.web;

import com.ftpix.hypetrain.web.controller.HypeTrainController;
import com.ftpix.mmath.DaoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import spark.Spark;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Import({DaoConfiguration.class})
public class HypeTrainConfiguration {

    private Logger logger = LogManager.getLogger();


    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(HypeTrainConfiguration.class);
    }

}
