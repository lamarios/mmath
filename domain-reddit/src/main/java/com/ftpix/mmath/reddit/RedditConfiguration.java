package com.ftpix.mmath.reddit;


import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedditConfiguration {
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
    private String redditBotOwner;

/*    @Bean
    public RedditClient redditClient() {

        Credentials oauthCreds = Credentials.script(redditUsername, redditPassword, redditClientId, redditSecret);
        UserAgent userAgent = new UserAgent("linux-docker", "science.mmathbro.bot", "1.0.0", redditBotOwner);
        return OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);
    }*/
}
