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

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OrientDBDao {
    private final String dbUrl;
    private final String username;
    private final String password;
    private final String dbname;
    public final static String VERTEX_FIGHTER = "fighter", EDGE_BEAT = "beat", SHERDOG_URL = "sherdog_url", FIGHT_ID = "fight_id";

    public final static String BETTER_THAN_QUERY = "select path.sherdog_url from (SELECT shortestPath( (SELECT FROM fighter WHERE sherdog_url='%s' ) , (SELECT FROM fighter WHERE sherdog_url='%s'), 'OUT', 'beat') AS path UNWIND path);";
    private Logger logger = LogManager.getLogger();

    public OrientDBDao(String dbUrl, String username, String password, String dbname) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
        this.dbname = dbname;


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
