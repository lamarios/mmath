package com.ftpix.mmath.cron;

import com.ftpix.mmath.cron.graph.GraphGenerator;
import com.ftpix.mmath.cron.stats.StatsRefresher;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebController {

    private final Refresh refresh;
    private final GraphGenerator graphGenerator;
    private final StatsRefresher statsRefresher;
    private final String mqAdminUrl;

    private AtomicBoolean graphRunning = new AtomicBoolean(false);
    private AtomicBoolean statsRunning = new AtomicBoolean(false);

    public WebController(Refresh refresh, GraphGenerator graphGenerator, StatsRefresher statsRefresher, String mqAdminUrl) {
        this.refresh = refresh;
        this.graphGenerator = graphGenerator;
        this.statsRefresher = statsRefresher;
        this.mqAdminUrl = mqAdminUrl;

        init();
    }

    private void init() {
        Spark.get("/refresh-mysql", this::refreshMySQL);
        Spark.get("/refresh-graph", this::refreshGraph);
        Spark.get("/refresh-stats", this::refreshStats);
        Spark.get("/queue-size", this::getQueueSize);
    }


    /**
     * Gets active mq queue size so we can use that endpoint to autoscale the slaves
     * @param request
     * @param response
     * @return
     */
    private int getQueueSize(Request request, Response response) {
        String username = "admin";
        String password = "admin";
        String login = username + ":" + password;
        String base64login = new String(Base64.encodeBase64(login.getBytes()));

        try {
            Document document = Jsoup
                    .connect("http://"+mqAdminUrl+"/admin/xml/queues.jsp")
                    .header("Authorization", "Basic " + base64login)
                    .get();

            return document.select("queues queue")
                    .stream()
                    .map(q -> q.select("stats"))
                    .map(s -> s.attr("size"))
                    .mapToInt(s -> Integer.parseInt(s))
                    .sum();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String refreshStats(Request request, Response response) {
        if (!statsRunning.get()) {
            new Thread(() -> {
                statsRunning.set(true);
                try {
                    statsRefresher.process();
                } finally {
                    statsRunning.set(false);
                }
            }).start();
            return "Refresh job started";
        } else {
            return "Job already started";
        }


    }


    private String refreshGraph(Request request, Response response) {
        if (!graphRunning.get()) {
            new Thread(() -> {
                graphRunning.set(true);
                try {
                    graphGenerator.process();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    graphRunning.set(false);
                }
            }).start();
            return "Refresh job started";
        } else {
            return "Job already started";
        }

    }

    private String refreshMySQL(Request request, Response response) {
        refresh.process();
        return "Refresh job started";

    }
}
