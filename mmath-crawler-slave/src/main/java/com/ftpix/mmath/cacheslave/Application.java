package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cacheslave.receivers.EventReceiver;
import com.ftpix.mmath.cacheslave.receivers.FighterReceiver;
import com.ftpix.mmath.cacheslave.receivers.OrganizationReceiver;
import com.ftpix.mmath.cacheslave.receivers.Receiver;
import com.ftpix.mmath.caching.CachingConfiguration;
import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.mmath.rabbitmq.RabbitmqConfiguration;
import com.ftpix.sherdogparser.Sherdog;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.JedisPool;

import static com.ftpix.mmath.rabbitmq.RabbitmqConfiguration.FIGHTER_QUEUE;
import static com.ftpix.mmath.rabbitmq.RabbitmqConfiguration.ORGANIZATION_QUEUE;

/**
 * Created by gz on 16-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, RabbitmqConfiguration.class, CachingConfiguration.class})
public class Application {




    @Bean
    Receiver fighterReceiver(RabbitTemplate fighterTemplate, RabbitTemplate eventTemplate, RabbitTemplate orgTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog, JedisPool jedisPool) {
        return new FighterReceiver(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog, jedisPool);
    }

    @Bean
    MessageListenerAdapter fighterListenerAdapter(Receiver fighterReceiver) {
        return new MessageListenerAdapter(fighterReceiver, "receiveMessageAsBytes");
    }


    @Bean
    Receiver eventReceiver(RabbitTemplate fighterTemplate, RabbitTemplate eventTemplate, RabbitTemplate orgTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog, JedisPool jedisPool) {
        return new EventReceiver(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog, jedisPool);
    }

    @Bean
    MessageListenerAdapter eventListenerAdapter(Receiver eventReceiver) {
        return new MessageListenerAdapter(eventReceiver, "receiveMessageAsBytes");
    }



    @Bean
    Receiver orgReceiver(RabbitTemplate fighterTemplate, RabbitTemplate eventTemplate, RabbitTemplate orgTemplate, FighterDao fighterDao, EventDao eventDao, OrganizationDao orgDao, Sherdog sherdog, JedisPool jedisPool) {
        return new OrganizationReceiver(fighterTemplate, orgTemplate, eventTemplate, fighterDao, eventDao, orgDao, sherdog, jedisPool);
    }

    @Bean
    MessageListenerAdapter orgListenerAdapter(Receiver orgReceiver) {
        return new MessageListenerAdapter(orgReceiver, "receiveMessageAsBytes");
    }

    @Bean
    SimpleMessageListenerContainer eventContainer(ConnectionFactory connectionFactory,
                                                  MessageListenerAdapter eventListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(RabbitmqConfiguration.EVENT_QUEUE);
        container.setMessageListener(eventListenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);

        return container;
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
    SimpleMessageListenerContainer orgContainer(ConnectionFactory connectionFactory,
                                                MessageListenerAdapter orgListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(ORGANIZATION_QUEUE);
        container.setMessageListener(orgListenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);

        return container;
    }

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);

        ShutdownTimer.start();
    }
}
