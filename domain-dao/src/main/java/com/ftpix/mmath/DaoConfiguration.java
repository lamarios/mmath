package com.ftpix.mmath;

import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.sherdog.SherdogConfiguration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@Import(SherdogConfiguration.class)
@ComponentScan("com.ftpix")
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
    public LocalSessionFactoryBean sessionFactory() throws PropertyVetoException, SQLException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(source());
        sessionFactory.setPackagesToScan(new String[] { "com.ftpix.mmath.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        return properties;
    }

    @Bean
    JdbcTemplate template(DataSource source) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(source);

        return jdbcTemplate;
    }

}
