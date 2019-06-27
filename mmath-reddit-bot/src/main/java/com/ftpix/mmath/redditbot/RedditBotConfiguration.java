package com.ftpix.mmath.redditbot;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.OrientDBDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

@Configuration
@Import({DaoConfiguration.class})
@ComponentScan("com.ftpix.mmath")
public class RedditBotConfiguration {

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(RedditBotConfiguration.class);

    }

}
