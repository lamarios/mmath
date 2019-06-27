package com.ftpix.mmath.web;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.web.controllers.EventsController;
import com.ftpix.mmath.web.controllers.MmathController;
import com.ftpix.mmath.web.controllers.StatsController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Created by gz on 26-Sep-16.
 */

@Configuration
@Import({DaoConfiguration.class})
@ComponentScan("com.ftpix.mmath")
public class WebConfiguration {

    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfiguration.class);
    }
}
