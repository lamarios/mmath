package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.cacheslave.graph.GraphGenerator;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebController {

    private final Refresh refresh;
    private final GraphGenerator graphGenerator;

    private AtomicBoolean running = new AtomicBoolean(false);

    public WebController(Refresh refresh, GraphGenerator graphGenerator) {
        this.refresh = refresh;
        this.graphGenerator = graphGenerator;

        init();
    }

    private void init() {
        Spark.get("/refresh-mysql", this::refreshMySQL);
        Spark.get("/refresh-graph", this::refreshGraph);
    }

    private String refreshGraph(Request request, Response response) {
        if(!running.get()) {
            new Thread(() -> {
                running.set(true);
                try {
                    graphGenerator.process();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                running.set(false);
            }).start();
        }

        return "Refresh job started";
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
