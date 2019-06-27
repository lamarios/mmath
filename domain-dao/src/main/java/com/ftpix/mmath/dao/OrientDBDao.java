package com.ftpix.mmath.dao;

import com.ftpix.mmath.model.MmathFighter;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.index.OIndexException;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Component
public class OrientDBDao {
    @Value("${ORIENTDB_URL:remote:mmath/}")
    private String dbUrl;

    @Value("${ORIENTDB_USER:root}")
    private String username;

    @Value("${ORIENTDB_DB_NAME:mmath}")
    private String dbname;

    @Value("${ORIENTDB_PASSWORD:password}")
    private String password;

    public final static String VERTEX_FIGHTER = "fighter", EDGE_BEAT = "beat", SHERDOG_URL = "sherdog_url", FIGHT_ID = "fight_id";

    public final static String BETTER_THAN_QUERY = "select path.sherdog_url from (SELECT shortestPath( (SELECT FROM fighter WHERE sherdog_url='%s' ) , (SELECT FROM fighter WHERE sherdog_url='%s'), 'OUT', 'beat') AS path UNWIND path);";
    public static final String DELETE_ALL_EDGES = "DELETE EDGE " + EDGE_BEAT + ";";
    public static final String DELETE_ALL_FIGHTERS = "DELETE VERTEX " + VERTEX_FIGHTER + ";";
    private Logger logger = LogManager.getLogger();

    @PostConstruct
    private void init(){
        try {
            createSchema();
            createClasses();
            createIndices();
        } catch (Exception e) {
            logger.error("Something went wrong went setting up the schema", e);
        }
    }

    /**
     * Creates the OrientDB Schema
     *
     * @throws IOException
     */
    private void createSchema() throws IOException {
        logger.info("Creating schema on orientDB if necessary");
        try {
            OServerAdmin admin = new OServerAdmin(dbUrl.replace("/", "")).connect(username, password);
            admin.createDatabase(dbname, "graph", "plocal");
            admin.close();
        } catch (ODatabaseException | OStorageException e) {
            logger.warn("The schema probably already exists: {}", e.getMessage());
        }


    }

    /**
     * Creates the required oreintdb classes
     */
    private void createClasses() {
        logger.info("Creating classes for OrientDB");
        OrientGraph graph = getGraph();

        try {
            try {
                OrientEdgeType edgeType = graph.createEdgeType(OrientDBDao.EDGE_BEAT);
                edgeType.createProperty(OrientDBDao.FIGHT_ID, OType.LONG);
            } catch (OSchemaException e) {
                logger.warn("Classes probably already exist: {}", e.getMessage());
            }

            try {

                OrientVertexType vertexType = graph.createVertexType(OrientDBDao.VERTEX_FIGHTER);
                vertexType.createProperty(OrientDBDao.SHERDOG_URL, OType.STRING);
            } catch (OSchemaException e) {
                logger.warn("Classes probably already exist: {}", e.getMessage());
            }
            graph.commit();
        } finally {
            graph.shutdown();
        }

    }


    /**
     * Creare required indices
     */
    private void createIndices() {
        OrientGraph graph = getGraph();
        logger.info("Creating indices for Orient DB classes");
        try {
            try {
                graph.createKeyIndex(SHERDOG_URL, Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", VERTEX_FIGHTER));
            } catch (OIndexException e) {
                logger.warn("Indices probably already exist: {}", e.getMessage());
            }

            try {
                graph.createKeyIndex(FIGHT_ID, Edge.class, new Parameter("type", "UNIQUE"), new Parameter("class", EDGE_BEAT));
            } catch (OIndexException e) {
                logger.warn("Indices probably already exist: {}", e.getMessage());
            }

            graph.commit();

        } catch (OIndexException e) {
            System.out.println(e.getClass());
            logger.warn("Indices probably already exist: {}", e.getMessage());
        } finally {
            graph.shutdown();
        }
    }


    /**
     * Gets a graph connection, easy to manage vertices and edges
     *
     * @return a graph connection
     */
    public OrientGraph getGraph() {
        return new OrientGraph(dbUrl + dbname, username, password);
    }

    /**
     * Gets a JDBC connection, easier to execute raw queries.
     *
     * @return the connection
     * @throws SQLException
     */
    public Connection getJDBCConnection() throws SQLException {
        Properties info = new Properties();
        info.put("user", username);
        info.put("password", password);

        return DriverManager.getConnection("jdbc:orient:" + dbUrl + dbname, info);
    }

    /**
     * Deletes all fighters from orientDB
     * @return
     * @throws SQLException
     */
    public boolean deleteAllFighters() throws SQLException {
        try (
                Connection con = getJDBCConnection();
                Statement statement = con.createStatement();
        ) {
            return statement.executeUpdate(OrientDBDao.DELETE_ALL_FIGHTERS) >= 0;
        }
    }


    /**
     * Deletes all Beat edges from orientdb
     * @return
     * @throws SQLException
     */
    public boolean deleteAllEdges() throws SQLException {
        try (
                Connection con = getJDBCConnection();
                Statement statement = con.createStatement();
        ) {
            return statement.executeUpdate(OrientDBDao.DELETE_ALL_EDGES) >= 0;
        }
    }

    /**
     * Finds the shortest path between two fighters
     * @param fighter1
     * @param fighter2
     * @return
     * @throws SQLException
     */
    public List<String> findShortestPath(MmathFighter fighter1, MmathFighter fighter2) throws SQLException {
        List<String> results = new ArrayList<>();
        try (
                Connection con = getJDBCConnection();
                Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery(String.format(OrientDBDao.BETTER_THAN_QUERY, fighter1.getSherdogUrl(), fighter2.getSherdogUrl()));
        ) {
            while (rs.next()) {
                results.add(rs.getString("path"));
            }
        }
        return results;
    }

}
