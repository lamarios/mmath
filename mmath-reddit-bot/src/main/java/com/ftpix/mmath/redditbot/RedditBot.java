package com.ftpix.mmath.redditbot;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.BarebonesPaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RedditBot {
    public static final int PAGE_SIZE = 100;
    private final Credentials credentials;
    private final UserAgent userAgent;
    private String subReddit = "all";
    private boolean running = false;
    private Consumer<Comment> onNewComment;
    private String lastSeen;
    private Predicate<Comment> commentFilter = c -> true;
    private long sleepDelay = 10000;

    private RedditBot(Credentials credentials, UserAgent userAgent) {
        this.credentials = credentials;
        this.userAgent = userAgent;
    }


    /**
     * starts the bot
     */
    public void start() {
        if (!running && subReddit.trim().length() > 0) {
            running = true;
            System.out.println("Starting bot");
            while (running) {
                fetchComments();
                try {
                    Thread.sleep(sleepDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Bot already running");
        }
    }

    /**
     * Starts the bot in a separate thread.
     */
    public void startAsync() {
        new Thread(this::start).start();
    }


    /**
     * Stops the bot
     */
    public void stop() {
        System.out.println("Stopping bot");
        running = false;
    }

    public RedditClient getClient() {
        return OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), credentials);
    }

    /**
     * Generate some sort of unique identifier for a comment
     * @param c
     * @return
     */
    private String getCommentId(Comment c){
        return c.getUrl()+c.getId()+c.getCreated();
    }

    /**
     * Will fetch the comments until the last seen
     */
    private void fetchComments() {
        RedditClient client = getClient();
        final BarebonesPaginator.Builder<Comment> commentsBuilder = client.subreddit(subReddit).comments();

        //if last seen doesn't exist, we just take the last comment and do nothing, just set last seen
        if (lastSeen == null) {
            System.out.println("We don't have a last seen, getting most recent comment");
            final BarebonesPaginator<Comment> lastComment = commentsBuilder.limit(1).build();
            final Listing<Comment> next = lastComment.next();
            if (next.size() > 0) {
                Comment c = next.get(0);
                lastSeen = getCommentId(c);
            }
            System.out.println("last seen -> " + lastSeen);
        } else {
            System.out.println("getting new comments");
            List<Comment> comments = new ArrayList<>();
            final BarebonesPaginator<Comment> build = commentsBuilder.limit(PAGE_SIZE).build();
            final Iterator<Listing<Comment>> iterator = build.iterator();

            PROCESS_COMMENTS:
            {
                while (iterator.hasNext()) {
                    final Listing<Comment> next = iterator.next();
                    for (Comment c : next) {
                        System.out.println("Comparing "+lastSeen+" with "+getCommentId(c));
                        if (!getCommentId(c).equalsIgnoreCase(lastSeen)) {
                            comments.add(c);
                        } else {
                            System.out.println("Seen all already");
                            break PROCESS_COMMENTS;
                        }
                    }

                }
            }

            System.out.println(comments.size() + " Comments to process");
            if (comments.size() > 0) {
                //when fetching, most recent comments come first
                lastSeen = getCommentId(comments.get(0));
                Collections.reverse(comments);

                comments.stream()
                        .filter(commentFilter)
                        .forEach(onNewComment);
            }

        }

    }

    private void setSubReddit(String subReddit) {
        this.subReddit = subReddit;
    }


    private void setOnNewComment(Consumer<Comment> onNewComment) {
        this.onNewComment = onNewComment;
    }

    private void setCommentFilter(Predicate<Comment> commentFilter) {
        this.commentFilter = commentFilter;
    }

    private void setSleepDelay(long sleepDelay) {
        this.sleepDelay = sleepDelay;
    }

    /**
     * Helper to create the bot
     */
    public static class Builder {

        private final RedditBot bot;

        public Builder(Credentials credentials, UserAgent userAgent) {
            bot = new RedditBot(credentials, userAgent);
        }

        /**
         * Sets the subreddit this bot should follow
         *
         * @param s the name of the subreddit (without /r/)
         * @return
         */
        public Builder followingSubReddit(String s) {
            bot.setSubReddit(s);
            return this;
        }

        /**
         * What to do when a new comment is available
         *
         * @param c a consumer
         * @return the builder itself
         */
        public Builder onNewComment(Consumer<Comment> c) {
            bot.setOnNewComment(c);
            return this;
        }

        /**
         * Optionally filter comments
         *
         * @param p the predicate used to filter comments passed to the onNewComment consumer.
         *          By default all new comments are sent to onNewComment
         * @return
         */
        public Builder filterComments(Predicate<Comment> p) {
            bot.setCommentFilter(p);
            return this;
        }

        /**
         * How long to wait before trying to get new comments
         *
         * @param ms the time to wait in milliseconds
         * @return
         */
        public Builder withPullDelay(long ms) {
            bot.setSleepDelay(ms);
            return this;
        }

        /**
         * Get the bot instance
         *
         * @return the reddit bot
         */
        public RedditBot build() {
            return bot;
        }

    }
}
