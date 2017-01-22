package com.ftpix.mmath;

import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.mmath.sherdog.SherdogConfiguration;
import com.ftpix.sherdogparser.Sherdog;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import(SherdogConfiguration.class)
public class DaoConfiguration {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.port}")
    private int mongoPort;

    @Value("${mongo.db}")
    private String mongoDb;


    ////////////////////
    /// Mongo DB
    /////////

    @Bean
    MongoClient mongoClient() {
        return new MongoClient(mongoHost, mongoPort);
    }

    @Bean
    MongoCollection<Document> fighterCollection(MongoClient mongoClient) {

        MongoDatabase db = mongoClient.getDatabase(mongoDb);

        try {
            db.createCollection("fighter");
        } catch (MongoCommandException e) {

        }
        return db.getCollection("fighter");
    }

    @Bean
    MongoCollection<Document> orgCollection(MongoClient mongoClient) {
        MongoDatabase db = mongoClient.getDatabase(mongoDb);

        try {
            db.createCollection("organization");
        } catch (MongoCommandException e) {

        }
        return db.getCollection("organization");
    }

    @Bean
    MongoCollection<Document> eventCollection(MongoClient mongoClient) {
        MongoDatabase db = mongoClient.getDatabase(mongoDb);

        try {
            db.createCollection("event");
        } catch (MongoCommandException e) {

        }
        return db.getCollection("event");
    }

    @Bean
    FighterDao fighterDao(MongoCollection<Document> fighterCollection, Sherdog sherdog){
        return new FighterDao(fighterCollection, sherdog);
    }


    @Bean
    EventDao eventDao(MongoCollection<Document> eventCollection, Sherdog sherdog){
        return new EventDao(eventCollection, sherdog);
    }


    @Bean
    OrganizationDao orgDao(MongoCollection<Document> orgCollection, Sherdog sherdog){
        return new OrganizationDao(orgCollection, sherdog);
    }


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(DaoConfiguration.class);



       FighterDao dao = (FighterDao) context.getBean("fighterDao");

        dao.get("http://www.sherdog.com/fighter/Alistair-Overeem-461").ifPresent(f->{
            System.out.println(f);
        });

    }
}
