package com.ftpix.mmath.cron.graph;

import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.sherdogparser.models.FightResult;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.ftpix.mmath.dao.OrientDBDao.FIGHT_ID;
import static com.ftpix.mmath.dao.OrientDBDao.SHERDOG_URL;


@Component
public class GraphGenerator {

    @Autowired
    private OrientDBDao orientDb;

    private Logger logger = LogManager.getLogger();


    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private FighterDAO fighterDAO;

    @Autowired
    private StatsEntryDAO statsEntryDAO;

    @Autowired
    private StatsCategoryDAO statsCategoryDAO;


    @Scheduled(cron = "0 0 23 ? * TUE")
    public void process() throws SQLException {
        logger.info("STARTING GRAPH JOB !!!");

        final OrientGraph graph = orientDb.getGraph();

        try {

            Map<String, Vertex> graphFighters = new HashMap<>();
            Map<Long, Edge> graphFights = new HashMap<>();

            logger.info("Cleaning all data before starting from scratch");
            orientDb.deleteAllEdges();
            orientDb.deleteAllFighters();

            logger.info("Getting all the processable fights");
            BatchProcessor.forClass(MmathFight.class, 100)
                    .withSupplier((batch, batchSize, offset) -> fightDAO.getBatch(offset, batchSize))
                    .withProcessor(fights -> fights
                            .stream()
                            .filter(f -> f.getFighter2() != null && f.getFighter1() != null && (f.getResult() == FightResult.FIGHTER_1_WIN || f.getResult() == FightResult.FIGHTER_2_WIN))
                            .filter(f -> !graphFights.containsKey(f.getId()))
                            .forEach(f -> addFightToGraph(f, graph, graphFights, graphFighters))
                    ).start();
        } finally {
            logger.info("Graph job done");
            graph.shutdown();
        }
    }

    /**
     * Will check if a fight exist and  if it doesn't add it to the graphDB
     *
     * @param fight         the fight to process
     * @param graph         the graph connection
     * @param graphFights   the list of all the fights in the graph DB
     * @param graphFighters the list of all the fighters in the graph DB
     */
    private void addFightToGraph(MmathFight fight, OrientGraph graph, Map<Long, Edge> graphFights, Map<String, Vertex> graphFighters) {

        logger.info("[{}] vs [{}] at event [{}]", fight.getFighter1().getSherdogUrl(), fight.getFighter2().getSherdogUrl(), fight.getEvent().getSherdogUrl());


        //we check if it already exists
        if (!graphFights.containsKey(fight.getId())) {
            logger.info("fight doesn't exist, adding it to graph");
            String winner;
            String loser;

            switch (fight.getResult()) {
                case FIGHTER_1_WIN:
                    winner = fight.getFighter1().getSherdogUrl();
                    loser = fight.getFighter2().getSherdogUrl();
                    break;
                case FIGHTER_2_WIN:
                    winner = fight.getFighter2().getSherdogUrl();
                    loser = fight.getFighter1().getSherdogUrl();
                    break;
                default:
                    //we need a winner to proceed
                    return;
            }

            //checking if our fighter exist
            Vertex winnerVertex = createOrGetFighter(winner, graph, graphFighters);
            Vertex loserVertex = createOrGetFighter(loser, graph, graphFighters);

            OrientEdge orientEdge = graph.addEdge(null, winnerVertex, loserVertex, OrientDBDao.EDGE_BEAT);
            orientEdge.setProperty(FIGHT_ID, fight.getId());
            orientEdge.save();

            graphFights.put(orientEdge.getProperty(FIGHT_ID), orientEdge);

            graph.commit();

        } else {
            logger.info("fight already exist... skipping");
        }

    }

    /**
     * Gets the fighter Vertex or create it if it doesn't exist
     *
     * @param sherdogUrl    the url of the fighter
     * @param graph         the orientDB graph
     * @param graphFighters the list of all the fighters on the graphDB
     * @return
     */
    private Vertex createOrGetFighter(String sherdogUrl, OrientGraph graph, Map<String, Vertex> graphFighters) {
        Vertex fighter;
        if (!graphFighters.containsKey(sherdogUrl)) {
            logger.info("fighter [{}] doesn't have a vertex, creating it", sherdogUrl);
            OrientVertex orientVertex = graph.addVertex(null);
            orientVertex.setProperty(SHERDOG_URL, sherdogUrl);
            orientVertex.moveToClass(OrientDBDao.VERTEX_FIGHTER);
            orientVertex.save();
            fighter = orientVertex;
            graphFighters.put(sherdogUrl, orientVertex);
        } else {
            logger.info("fighter [{}] already exists", sherdogUrl);
            fighter = graphFighters.get(sherdogUrl);
        }

        return fighter;

    }
}
