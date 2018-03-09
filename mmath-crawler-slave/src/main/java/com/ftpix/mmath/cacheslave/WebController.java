package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.cacheslave.graph.GraphGenerator;
import com.ftpix.mmath.cacheslave.stats.StatsRefresher;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebController {

    private final Refresh refresh;
    private final GraphGenerator graphGenerator;
    private final StatsRefresher statsRefresher;

    private AtomicBoolean graphRunning = new AtomicBoolean(false);
    private AtomicBoolean statsRunning = new AtomicBoolean(false);

    public WebController(Refresh refresh, GraphGenerator graphGenerator, StatsRefresher statsRefresher) {
        this.refresh = refresh;
        this.graphGenerator = graphGenerator;
        this.statsRefresher = statsRefresher;

        init();
    }

    private void init() {
        Spark.get("/refresh-mysql", this::refreshMySQL);
        Spark.get("/refresh-graph", this::refreshGraph);
        Spark.get("/refresh-stats", this::refreshStats);
    }

    private String refreshStats(Request request, Response response) {
        if (!statsRunning.get()) {
            new Thread(() -> {
                statsRunning.set(true);
                try {
                    statsRefresher.process();
                }finally {
                    statsRunning.set(false);
                }
            }).start();
            return "Refresh job started";
        }else{
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
                }finally {
                    graphRunning.set(false);
                }
            }).start();
            return "Refresh job started";
        }else{
            return "Job already started";
        }

    }

    private String refreshMySQL(Request request, Response response) {
        try {
            refresh.process();
            return "Refresh job started";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Refresh job failed to start";
        }

    }
}
