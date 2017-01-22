package com.ftpix.mmath.web;

import com.google.gson.Gson;

import com.ftpix.calculator.client.CalculatorClient;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.web.models.Query;
import com.ftpix.utils.GsonUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Created by gz on 26-Sep-16.
 */
public class WebServer {

    private final int port;
    private final CalculatorClient client;

    private final FighterDao fighterDao;

    private Logger logger = LogManager.getLogger();

    private final Gson gson = GsonUtils.getGson();

    private final String crawlerCacheFolder;
    private final String webCacheFolder;

    private final static String CACHE_WEB_PATH = "cache/pictures";

    public WebServer(int port, String webCacheFolder, String crawlerCacheFolder, CalculatorClient client, FighterDao fighterDao) {
        this.port = port;
        this.client = client;
        this.fighterDao = fighterDao;

        this.webCacheFolder = webCacheFolder;
        this.crawlerCacheFolder = crawlerCacheFolder;
    }

    public void startServer() {
        Spark.port(port);

        Spark.staticFiles.externalLocation("C:\\Users\\gz\\Dev\\ideaProjects\\mmath\\mmath-web\\src\\web");

        Spark.before("*", this::logRequest);
        Spark.before("/api/*",this::jsonRequest);
        Spark.get("/api/better-than/:fighter1/:fighter2", "application/json", this::betterThan, gson::toJson);
        Spark.post("/api/fighters/query", "application/json", this::searchFighter, gson::toJson);
        Spark.get("/api/fighters/:id", "application/json", this::getFighter, gson::toJson);
        Spark.get("/cache/pictures/*", this::getFighterPicture);
    }

    /**
     * Get an image from the cache path
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private Object getFighterPicture(Request request, Response response) throws IOException {

        String localPath = webCacheFolder+ File.separator+request.splat()[0];


        File file = new File(localPath);
        logger.info("Path [{}] Looking for file [{}]", localPath, file.getAbsolutePath());

        if (file.exists()) {
            response.raw().setContentType("application/octet-stream");
            response.raw().setHeader("Content-Disposition", "inline; filename=" + file.getName());

            FileInputStream in = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                response.raw().getOutputStream().write(buffer, 0, len);
            }

            in.close();
            return response.raw();
        } else {
            response.status(404);
            return "";
        }
    }

    private MmathFighter getFighter(Request request, Response response) {
        return fighterDao.get(request.params(":id")).get();
    }

    private void jsonRequest(Request request, Response response) {
        response.type("application/json");
    }

    private List<MmathFighter> searchFighter(Request request, Response response) {
        try {
            Query query = gson.fromJson(request.body(), Query.class);

            List<MmathFighter> fighters =  fighterDao.findByName(query.getName(), 10);
            fighters.parallelStream().forEach(this::updateCachePath);

            return fighters;
        }catch (Exception e){
            logger.error(e);
            return null;
        }
    }

    private List<MmathFighter> betterThan(Request request, Response response) throws IOException {
        try {
            String fighter1 = request.params(":fighter1");
            String fighter2 = request.params(":fighter2");

            logger.info("{} vs {}", fighter1, fighter2);

            List<MmathFighter> result =  client.betterThan(fighter1, fighter2).execute().body();
            result.parallelStream().forEach(this::updateCachePath);
            logger.info("Result size: {}",result.size());
            return result;
        }catch(Exception e){
            logger.error("error: ",e);
            return new ArrayList<>();
        }
    }

    private void logRequest(Request request, Response response) {
        logger.info("{} {}", request.requestMethod(), request.url());

    }

    /**
     * Updates the path of the picture relative to the webserver
     * @param fighter
     */
    private void updateCachePath(MmathFighter fighter){
        fighter.setPicture(fighter.getPicture().replace(crawlerCacheFolder, Matcher.quoteReplacement(CACHE_WEB_PATH)));
    }
}
