package com.ftpix.mmath.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class MqConfiguration {
    @Value("${MQ_URL:tcp://mmath:61616}")
    String brokerUrl;

    @Value("${MQ_FIGHTERS:fighters}")
    String fighterTopic;

    @Value("${MQ_EVENTS:events}")
    String eventTopic;

    @Value("${MQ_ORGS:orgs}")
    String organizationTopic;



    @Bean
    String fighterTopic(){
        return fighterTopic;
    }

    @Bean
    String eventTopic(){
        return eventTopic;
    }

    @Bean
    String organizationTopic(){
        return organizationTopic;
    }


    @Bean
    PooledConnectionFactory poolMqConnection(ActiveMQConnectionFactory connectionFactory){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);

        return pooledConnectionFactory;
    }

    @Bean
    ActiveMQConnectionFactory connectionFactory() {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);

        return factory;
    }


    @Bean
    JmsTemplate jmsTemplate(PooledConnectionFactory pooledConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(pooledConnectionFactory);

        return jmsTemplate;
    }



}
