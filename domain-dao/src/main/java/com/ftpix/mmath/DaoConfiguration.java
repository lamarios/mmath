package com.ftpix.mmath;

import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.sherdog.SherdogConfiguration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@Import(SherdogConfiguration.class)
public class DaoConfiguration {


    @Value("${DB_URL:jdbc:mysql://mmath/}")
    private String dbUrl;

    @Value("${DB_NAME:mmath}")
    private String dbName;

    @Value("${DB_USER:root}")
    private String username;

    @Value("${DB_PASSWORD:password}")
    private String password;

    @Value("${DB_OPTIONS:?autoReconnect=true&useSSL=false}")
    private String options;


    @Value("${ORIENTDB_URL:remote:mmath/}")
    private String orientdbUrl;

    @Value("${ORIENTDB_USER:root}")
    private String orientdbUsername;

    @Value("${ORIENTDB_DB_NAME:mmath}")
    private String orientdbName;

    @Value("${ORIENTDB_PASSWORD:password}")
    private String orientdbPassword;

    public DaoConfiguration() {
    }

    @Bean
    public DataSource source() throws SQLException, PropertyVetoException {

        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setPassword(password);
        ds.setJdbcUrl(dbUrl+options);
        ds.setUser(username);
        //Creating DB if not exist
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(ds);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + dbName);

        ds.close();



        ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setPassword(password);
        ds.setJdbcUrl(dbUrl + dbName+options);
        ds.setUser(username);
        ds.setAutomaticTestTable("jdbc_test");
        ds.setTestConnectionOnCheckin(true);
        ds.setIdleConnectionTestPeriod(300);
//        ComboPooledDataSource );
        ds.setMaxPoolSize(10);

        return ds;
    }

    @Bean
    JdbcTemplate template(DataSource source) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(source);


        return jdbcTemplate;
    }

    @Bean
    public MySQLDao dao(JdbcTemplate template) {
        return new MySQLDao(template);
    }

    @Bean
    public OrientDBDao orientDBDao() {
        OrientDBDao dao = new OrientDBDao(orientdbUrl, orientdbUsername, orientdbPassword, orientdbName);


        return dao;
    }
}
