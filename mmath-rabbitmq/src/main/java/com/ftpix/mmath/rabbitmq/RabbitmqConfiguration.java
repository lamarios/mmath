package com.ftpix.mmath.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gz on 12-Feb-17.
 */
@Configuration
public class RabbitmqConfiguration {
    public final static String FIGHTER_QUEUE = "fighter.crawl", EVENT_QUEUE = "event.crawl", ORGANIZATION_QUEUE = "org.crawl";
    public final static String MMATH_EXCHANGE = "rmq.mmath.exchange";
    public final static String FIGHTER_ROUTING_KEY = "fighter.key", EVENT_ROUTING_KEY = "event.key", ORGANIZATION_ROUTING_KEY = "org.key";
    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${rabbitmq.user}")
    private String rabbitmqUsername;
    @Value("${rabbitmq.pass}")
    private String rabbitmqPassword;




    //////////////////////////
    ///RabbitMq
    ///////////////
    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory con = new CachingConnectionFactory(rabbitmqHost, rabbitmqPort);
        con.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        con.setUsername(rabbitmqUsername);
        con.setPassword(rabbitmqPassword);
        return con;
    }


    @Bean
    DirectExchange mmathExchange(RabbitAdmin admin) {
        DirectExchange exchange = new DirectExchange(MMATH_EXCHANGE, true, false);
        admin.declareExchange(exchange);
        return exchange;
    }


    ///////////////////////////
    ///// admin
    //////////////

    @Bean
    RabbitAdmin admin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /////////////////////////////
    ///// Fighter Queue
    //////////////////////////////
    @Bean
    public Queue fighterQueue(RabbitAdmin admin) {
        Queue queue = new Queue(FIGHTER_QUEUE, true);
        admin.declareQueue(queue);
        return queue;
    }

    @Bean
    Binding fighterExchangeBinding(DirectExchange mmathExchange, Queue fighterQueue, RabbitAdmin admin) {

        Binding binding = BindingBuilder.bind(fighterQueue).to(mmathExchange).with(FIGHTER_ROUTING_KEY);
        admin.declareBinding(binding);
        return binding;
    }



    @Bean
    public RabbitTemplate fighterTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate r = new RabbitTemplate(connectionFactory);
        r.setExchange(MMATH_EXCHANGE);
        r.setRoutingKey(FIGHTER_ROUTING_KEY);
        r.setQueue(FIGHTER_QUEUE);
        r.setConnectionFactory(connectionFactory);
        return r;
    }

    /////////////////////////////
    ///// Event Queue
    //////////////////////////////
    @Bean
    public Queue eventQueue(RabbitAdmin admin) {
        Queue queue = new Queue(EVENT_QUEUE, true);
        admin.declareQueue(queue);
        return queue;
    }

    @Bean
    Binding eventExchangeBinding(DirectExchange mmathExchange, Queue eventQueue, RabbitAdmin admin) {

        Binding binding = BindingBuilder.bind(eventQueue).to(mmathExchange).with(EVENT_ROUTING_KEY);
        admin.declareBinding(binding);
        return binding;
    }





    @Bean
    public RabbitTemplate eventTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate r = new RabbitTemplate(connectionFactory);
        r.setExchange(MMATH_EXCHANGE);
        r.setRoutingKey(EVENT_ROUTING_KEY);
        r.setQueue(EVENT_QUEUE);
        r.setConnectionFactory(connectionFactory);
        return r;
    }

    /////////////////////////////
    ///// Org Queue
    //////////////////////////////
    @Bean
    public Queue orgQueue(RabbitAdmin admin) {
        Queue queue = new Queue(ORGANIZATION_QUEUE, true);
        admin.declareQueue(queue);
        return queue;
    }

    @Bean
    Binding orgExchangeBinding(DirectExchange mmathExchange, Queue orgQueue, RabbitAdmin admin) {
        Binding binding = BindingBuilder.bind(orgQueue).to(mmathExchange).with(ORGANIZATION_ROUTING_KEY);
        admin.declareBinding(binding);
        return binding;

    }



    @Bean
    public RabbitTemplate orgTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate r = new RabbitTemplate(connectionFactory);
        r.setExchange(MMATH_EXCHANGE);
        r.setRoutingKey(ORGANIZATION_ROUTING_KEY);
        r.setQueue(ORGANIZATION_QUEUE);
        r.setConnectionFactory(connectionFactory);
        return r;
    }
}
