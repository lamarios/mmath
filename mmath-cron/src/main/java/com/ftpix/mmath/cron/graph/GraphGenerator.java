package com.ftpix.mmath.cron.graph;

import com.ftpix.mmath.cron.utils.BatchProcessor;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.FightResult;
import com.orientechnologies.orient.core.id.ORecordId;
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
import java.util.Optional;

import static com.ftpix.mmath.dao.OrientDBDao.*;


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
            logger.info("Getting all the processable fights");
            BatchProcessor.forClass(MmathFight.class, 100)
                    .withSupplier((batch, batchSize, offset) -> fightDAO.getBatch(offset, batchSize))
                    .withProcessor(fights -> fights
                            .stream()
                            .filter(f -> f.getFighter2() != null && f.getFighter1() != null && (f.getResult() == FightResult.FIGHTER_1_WIN || f.getResult() == FightResult.FIGHTER_2_WIN))
                            .filter(f -> {
                               for(Edge e:  graph.getEdges(EDGE_BEAT+"."+FIGHT_ID, f.getId())){
                                   return false;
                               }
                               return true;
                            })
                            .forEach(f -> addFightToGraph(f, graph))
                    ).start();
        } finally {
            logger.info("Graph job done");
            graph.shutdown();
        }
    }

    /**
     * Will check if a fight exist and  if it doesn't add it to the graphDB
     *
     * @param fight the fight to process
     * @param graph the graph connection
     */
    private void addFightToGraph(MmathFight fight, OrientGraph graph) {

        logger.info("[{}] vs [{}] at event [{}]", fight.getFighter1().getSherdogUrl(), fight.getFighter2().getSherdogUrl(), fight.getEvent().getSherdogUrl());


        //we check if it already exists
        logger.info("fight doesn't exist, adding it to graph");
        MmathFighter winner;
        MmathFighter loser;

        switch (fight.getResult()) {
            case FIGHTER_1_WIN:
                winner = fight.getFighter1();
                loser = fight.getFighter2();
                break;
            case FIGHTER_2_WIN:
                winner = fight.getFighter2();
                loser = fight.getFighter1();
                break;
            default:
                //we need a winner to proceed
                return;
        }

        //checking if our fighter exist
        Vertex winnerVertex = createOrGetFighter(winner, graph);
        Vertex loserVertex = createOrGetFighter(loser, graph);

        OrientEdge orientEdge = graph.addEdge(null, winnerVertex, loserVertex, OrientDBDao.EDGE_BEAT);
        orientEdge.setProperty(FIGHT_ID, fight.getId());
        orientEdge.save();

        graph.commit();

    }

    /**
     * Gets the fighter Vertex or create it if it doesn't exist
     *
     * @param f     the fighter
     * @param graph the orientDB graph
     * @return
     */
    private Vertex createOrGetFighter(MmathFighter f, OrientGraph graph) {
        Vertex fighter = null;
        for (Vertex v : graph.getVertices(VERTEX_FIGHTER+"."+SHERDOG_URL, f.getSherdogUrl())) {
            fighter = v;
        }

        if (fighter == null) {
            logger.info("fighter [{}] doesn't have a vertex, creating it", f.getSherdogUrl());
            OrientVertex orientVertex = graph.addVertex(null);
            orientVertex.setProperty(SHERDOG_URL, f.getSherdogUrl());
            orientVertex.moveToClass(OrientDBDao.VERTEX_FIGHTER);
            orientVertex.save();
            fighter = orientVertex;
        } else {
            logger.info("fighter [{}] already exists", f.getSherdogUrl());
        }

        return fighter;

    }
}
