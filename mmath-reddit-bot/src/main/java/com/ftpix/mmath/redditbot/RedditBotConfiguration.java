package com.ftpix.mmath.redditbot;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class})
public class RedditBotConfiguration {
    @Value("${reddit.sub}")
    private String subreddit;


    @Bean
    MmathBot mmathBot(MySQLDao dao, OrientDBDao orientDBDao) {
        return new MmathBot(dao, orientDBDao, subreddit);
    }


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(RedditBotConfiguration.class);

    }

}
