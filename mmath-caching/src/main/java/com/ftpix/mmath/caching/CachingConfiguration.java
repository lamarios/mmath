package com.ftpix.mmath.caching;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.rabbitmq.RabbitmqConfiguration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by gz on 12-Feb-17.
 */

@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, RabbitmqConfiguration.class})
public class CachingConfiguration {

    @Value("${redis.url}")
    private String redisUrl;

    @Value("${redis.port}")
    private int redisPort;

    @Bean
    JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        JedisPool pool = new JedisPool(config, redisUrl, redisPort);

        return pool;
    }


    ///////
    // Caching Beans
    @Bean
    FighterCache fighterCache(JedisPool jedisPool, FighterDao fighterDao, RabbitTemplate fighterTemplate) {
        return new FighterCache(jedisPool, fighterDao, fighterTemplate);
    }

}
