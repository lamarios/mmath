package com.ftpix.mmath.refresh;

import com.ftpix.calculator.client.CalculatorClient;
import com.ftpix.calculator.client.CalculatorClientConfiguration;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.FighterDao;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;

/**
 * Created by gz on 25-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, CalculatorClientConfiguration.class})
public class RefreshConfiguration {

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


    @Bean
    public RabbitTemplate fighterTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate r = new RabbitTemplate(connectionFactory);
        r.setExchange(MMATH_EXCHANGE);
        r.setRoutingKey(FIGHTER_ROUTING_KEY);
        r.setQueue(FIGHTER_QUEUE);
        r.setConnectionFactory(connectionFactory);
        return r;
    }

    ///////////////////////////
    ///// Quartz
    //////////////
    @Bean
    JobDetail jobDetail(Refresh refresh) throws Exception {
        MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
        job.setTargetObject(refresh);
        job.setTargetMethod("process");
        job.setName("refresh_db");
        job.setConcurrent(false);

        job.afterPropertiesSet();


        return job.getObject();
    }

    @Bean
    Trigger cronTrigger(JobDetail jobDetail) throws ParseException {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
      trigger.setCronExpression("00 30 00 * * ?");
       // trigger.setCronExpression("00 * * * * ?");
        trigger.setName("daily");

        trigger.afterPropertiesSet();

        return trigger.getObject();
    }

    @Bean
    Refresh refresh(RabbitTemplate fighterTemplate, FighterDao dao, CalculatorClient calculatorClient) {
        return new Refresh(fighterTemplate, dao, calculatorClient);
    }

    @Bean
    Scheduler schedulerFactory(Trigger cronTrigger, JobDetail jobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(jobDetail);
        scheduler.setTriggers(cronTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(RefreshConfiguration.class);
    }
}
