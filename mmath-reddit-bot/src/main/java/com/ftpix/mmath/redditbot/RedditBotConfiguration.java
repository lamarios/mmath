package com.ftpix.mmath.redditbot;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

@Configuration
@Import({DaoConfiguration.class})
public class RedditBotConfiguration {


    @Value("${REDDIT_CLIENT_ID}")
    private String redditClientId;

    @Value("${REDDIT_SECRET}")
    private String redditSecret;


    @Value("${REDDIT_USERNAME}")
    private String redditUsername;


    @Value("${REDDIT_PASSWORD}")
    private String redditPassword;


    @Value("${REDDIT_SUB}")
    private String subreddit;


    @Value("${REDDIT_BOT_OWNER}")
    private String redditOwner;



    @Bean
    MmathBot mmathBot(MySQLDao dao, OrientDBDao orientDBDao) {
        return new MmathBot(dao, orientDBDao, redditUsername, redditPassword, redditClientId, redditSecret, subreddit, redditOwner);
    }


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(RedditBotConfiguration.class);

    }

}
