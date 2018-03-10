package com.ftpix.mmath.web;

import com.ftpix.utils.GsonUtils;
import com.google.gson.Gson;
import mmath.S3Helper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Created by gz on 26-Sep-16.
 */
public class WebServer {

    private final int port;

    private final S3Helper s3Helper;

    private Logger logger = LogManager.getLogger();
    private final Gson gson = GsonUtils.getGson();


    public WebServer(int port,  S3Helper s3Helper) {
        this.port = port;
        this.s3Helper = s3Helper;
    }

    public void startServer() {
        Spark.port(port);

        if (System.getProperty("dev", "false").equalsIgnoreCase("true")) {
            logger.info("DEV MODE");
            Spark.externalStaticFileLocation("/home/gz/IdeaProjects/mmath/mmath-web/src/main/resources/web/public");

        } else {
            Spark.staticFiles.location("/web/public");
        }

        Spark.before("*", this::logRequest);
        Spark.before("/api/*", this::jsonRequest);

        Spark.get("/pictures/*", this::getFighterPicture);

        //front end endpoints
        Spark.get("/:fighter1/vs/:fighter2", this::serveIndex);
        Spark.get("/events", this::serveIndex);
        Spark.get("/events/:id/fights", this::serveIndex);
        Spark.get("/stats", this::serveIndex);
        Spark.get("/stats/:cat", this::serveIndex);

        Spark.exception(Exception.class, (e, request, response) -> {
            logger.error("Error while processing request", e);
            response.status(503);
            response.body(e.getMessage());
        });
    }


    /**
     * Serves the content of the index file;
     *
     * @param request
     * @param response
     * @return
     */
    private String serveIndex(Request request, Response response) throws IOException {
        try (
                InputStream inputStream = getClass().getClassLoader().getResource("web/public/index.html").openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            return reader.lines().collect(Collectors.joining(""));
        }
    }


    /**
     * Get an image from the cache path
     */
    private Object getFighterPicture(Request request, Response response) throws Exception {

        String fighterHash = request.splat()[0];
        response.raw().setContentType("application/octet-stream");
        response.raw().setHeader("Content-Disposition", "inline; filename=" + fighterHash);

        InputStream in;
        if (fighterHash.equalsIgnoreCase("default.jpg")) {
            in = getClass().getClassLoader().getResource("web/public/images/fighterPlaceHolder.gif").openStream();
        } else {
            in = s3Helper.getFile(fighterHash);
        }

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                response.raw().getOutputStream().write(buffer, 0, len);
            }

            return response.raw();

        } finally {
            in.close();
        }
    }


    private void jsonRequest(Request request, Response response) {
        response.type("application/json");
    }




    private void logRequest(Request request, Response response) {
        logger.info("{} {}", request.requestMethod(), request.url());

    }

}
