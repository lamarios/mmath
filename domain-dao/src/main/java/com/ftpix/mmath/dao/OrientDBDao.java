package com.ftpix.mmath.dao;

import com.ftpix.mmath.model.MmathFighter;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OrientDBDao {
    private final String dbUrl, username, password;
    public final static String VERTEX_FIGHTER = "fighter", EDGE_BEAT = "beat", SHERDOG_URL = "sherdog_url", FIGHT_ID = "fight_id";

    public final static String BETTER_THAN_QUERY = "select path.sherdog_url from (SELECT shortestPath( (SELECT FROM fighter WHERE sherdog_url='%s' ) , (SELECT FROM fighter WHERE sherdog_url='%s'), 'OUT', 'beat') AS path UNWIND path);";

    public OrientDBDao(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }


    /**
     * Gets a graph connection, easy to manage vertices and edges
     *
     * @return a graph connection
     */
    public OrientGraph getGraph() {
        return new OrientGraph(dbUrl, username, password);
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

        return (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:" + dbUrl, info);
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
