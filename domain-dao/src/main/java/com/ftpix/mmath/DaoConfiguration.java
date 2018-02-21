package com.ftpix.mmath;

import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.mmath.sherdog.SherdogConfiguration;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.sql.SQLException;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import(SherdogConfiguration.class)
public class DaoConfiguration {


    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;


    @Value("${orientdb.url}")
    private String orientdbUrl;

    @Value("${orientdb.user}")
    private String orientdbUsername;

    @Value("${orientdb.password}")
    private String orientdbPassword;

    public DaoConfiguration() {
    }

    @Bean
    public JdbcConnectionSource source() throws SQLException {
        return new JdbcConnectionSource(dbUrl, username, password);
    }

    @Bean
    public Dao<MmathEvent, String> eventDao(JdbcConnectionSource source) throws SQLException {
        Dao<MmathEvent, String> dao = DaoManager.createDao(source, MmathEvent.class);
        TableUtils.createTableIfNotExists(source, MmathEvent.class);

        return dao;
    }

    @Bean
    public Dao<MmathFighter, String> fighterDao(JdbcConnectionSource source) throws SQLException {
        Dao<MmathFighter, String> dao = DaoManager.createDao(source, MmathFighter.class);
        TableUtils.createTableIfNotExists(source, MmathFighter.class);


        return dao;
    }

    @Bean
    public Dao<MmathFight, Long> fightDao(JdbcConnectionSource source) throws SQLException {
        Dao<MmathFight, Long> dao = DaoManager.createDao(source, MmathFight.class);
        TableUtils.createTableIfNotExists(source, MmathFight.class);

        return dao;
    }

    @Bean
    public Dao<MmathOrganization, String> orgDao(JdbcConnectionSource source) throws SQLException {
        Dao<MmathOrganization, String> dao = DaoManager.createDao(source, MmathOrganization.class);
        TableUtils.createTableIfNotExists(source, MmathOrganization.class);

        return dao;
    }

    @Bean
    public OrientDBDao orientDBDao(){
       OrientDBDao dao = new OrientDBDao(orientdbUrl, orientdbUsername, orientdbPassword);

        OrientGraph graph = dao.getGraph();
       try{
           OrientEdgeType edgeType = graph.createEdgeType(OrientDBDao.EDGE_BEAT);
           edgeType.createProperty(OrientDBDao.FIGHT_ID, OType.LONG);

           OrientVertexType vertexType = graph.createVertexType(OrientDBDao.VERTEX_FIGHTER);
           vertexType.createProperty(OrientDBDao.SHERDOG_URL, OType.STRING);
           graph.commit();
       }catch(OSchemaException e){
          //classes probably already exist
       }finally {
           graph.shutdown();
       }

       return dao;
    }
}
