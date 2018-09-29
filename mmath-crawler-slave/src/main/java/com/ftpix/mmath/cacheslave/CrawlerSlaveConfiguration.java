package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cacheslave.processors.EventProcessor;
import com.ftpix.mmath.cacheslave.processors.FighterProcessor;
import com.ftpix.mmath.cacheslave.processors.OrganizationProcessor;
import com.ftpix.mmath.cacheslave.processors.Processor;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.mq.MqConfiguration;
import com.ftpix.sherdogparser.Sherdog;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Created by gz on 16-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, MqConfiguration.class})
public class CrawlerSlaveConfiguration {
    //Receiver

    @Bean
    EventProcessor eventProcessor(MySQLDao dao, JmsTemplate jmsTemplate, Sherdog sherdog, String fighterTopic, String eventTopic, String organizationTopic) {
        return new EventProcessor(dao, jmsTemplate, sherdog, fighterTopic, eventTopic, organizationTopic);
    }


    @Bean
    FighterProcessor fighterProcessor(MySQLDao dao, JmsTemplate jmsTemplate, Sherdog sherdog, String fighterTopic, String eventTopic, String organizationTopic) {
        return new FighterProcessor(dao, jmsTemplate, sherdog, fighterTopic, eventTopic, organizationTopic);
    }

    @Bean
    OrganizationProcessor organizationProcessor(MySQLDao dao, JmsTemplate jmsTemplate, Sherdog sherdog, String fighterTopic, String eventTopic, String organizationTopic) {
        return new OrganizationProcessor(dao, jmsTemplate, sherdog, fighterTopic, eventTopic, organizationTopic);
    }


    @Bean
    DefaultMessageListenerContainer eventListener(PooledConnectionFactory pooledConnectionFactory, String eventTopic, EventProcessor eventProcessor) {
        return createListener(pooledConnectionFactory, eventTopic, eventProcessor);
    }

    @Bean
    DefaultMessageListenerContainer fighterListener(PooledConnectionFactory pooledConnectionFactory, String fighterTopic, FighterProcessor fighterProcessor) {
        return createListener(pooledConnectionFactory, fighterTopic, fighterProcessor);
    }

    @Bean
    DefaultMessageListenerContainer organiztionListener(PooledConnectionFactory pooledConnectionFactory, String organizationTopic, OrganizationProcessor organizationProcessor) {
        return createListener(pooledConnectionFactory, organizationTopic, organizationProcessor);
    }

    private DefaultMessageListenerContainer  createListener(PooledConnectionFactory connectionFactory, String topic, Processor processor){
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestinationName(topic);
        defaultMessageListenerContainer.setMessageListener(processor);
        return defaultMessageListenerContainer;
    }

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CrawlerSlaveConfiguration.class);

    }

}
