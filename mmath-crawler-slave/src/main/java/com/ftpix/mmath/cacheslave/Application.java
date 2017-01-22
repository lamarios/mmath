package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cacheslave.receivers.EventReceiver;
import com.ftpix.mmath.cacheslave.receivers.FighterReceiver;
import com.ftpix.mmath.cacheslave.receivers.OrganizationReceiver;
import com.ftpix.mmath.cacheslave.receivers.Receiver;
import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.sherdogparser.Sherdog;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gz on 16-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import(DaoConfiguration.class)
public class Application {

    private final static String FIGHTER_QUEUE = "fighter.crawl", EVENT_QUEUE = "event.crawl", ORGANIZATION_QUEUE = "org.crawl";
    private final static String MMATH_EXCHANGE = "rmq.mmath.exchange";
    private final static String FIGHTER_ROUTING_KEY = "fighter.key", EVENT_ROUTING_KEY = "event.key", ORGANIZATION_ROUTING_KEY = "org.key";
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
    SimpleMessageListenerContainer fighterContainer(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter fighterListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(FIGHTER_QUEUE);
        container.setMessageListener(fighterListenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        return container;
    }

    @Bean
    Receiver fighterReceiver(RabbitTemplate fighterTemplate, RabbitTemplate eventTemplate, RabbitTemplate orgTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog) {
        return new FighterReceiver(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog);
    }

    @Bean
    MessageListenerAdapter fighterListenerAdapter(Receiver fighterReceiver) {
        return new MessageListenerAdapter(fighterReceiver, "receiveMessageAsBytes");
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
    SimpleMessageListenerContainer eventContainer(ConnectionFactory connectionFactory,
                                                  MessageListenerAdapter eventListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(EVENT_QUEUE);
        container.setMessageListener(eventListenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);

        return container;
    }

    @Bean
    Receiver eventReceiver(RabbitTemplate fighterTemplate, RabbitTemplate eventTemplate, RabbitTemplate orgTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog) {
        return new EventReceiver(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog);
    }

    @Bean
    MessageListenerAdapter eventListenerAdapter(Receiver eventReceiver) {
        return new MessageListenerAdapter(eventReceiver, "receiveMessageAsBytes");
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
    SimpleMessageListenerContainer orgContainer(ConnectionFactory connectionFactory,
                                                MessageListenerAdapter orgListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(ORGANIZATION_QUEUE);
        container.setMessageListener(orgListenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);

        return container;
    }

    @Bean
    Receiver orgReceiver(RabbitTemplate fighterTemplate, RabbitTemplate eventTemplate, RabbitTemplate orgTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog) {
        return new OrganizationReceiver(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog);
    }

    @Bean
    MessageListenerAdapter orgListenerAdapter(Receiver orgReceiver) {
        return new MessageListenerAdapter(orgReceiver, "receiveMessageAsBytes");
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


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);

        ShutdownTimer.start();
    }
}
