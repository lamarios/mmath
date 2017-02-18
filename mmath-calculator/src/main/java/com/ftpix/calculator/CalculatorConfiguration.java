package com.ftpix.calculator;

import com.ftpix.calculator.cache.CacheHandler;
import com.ftpix.calculator.web.WebServer;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.text.ParseException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gz on 18-Sep-16.
 */
@Configuration
@Import({DaoConfiguration.class})
@PropertySource("classpath:config.properties")
public class CalculatorConfiguration {

    @Value("${calculator.port}")
    private int port;

    private final Logger logger = LogManager.getLogger();

    @Bean
    BetterThan betterThan(Map<String, MmathFighter> fighterCache) {
        return new BetterThan(fighterCache);
    }

    @Bean
    WebServer webServer(BetterThan betterThan, Map<String, MmathFighter> fighterCache) {
        WebServer server = new WebServer(betterThan, fighterCache, port);
        server.setupServer();

        return server;
    }

    @Bean
    Map<String, MmathFighter> fighterCache(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    CacheHandler cacheHandler(FighterDao fighterDao, Map<String, MmathFighter> fighterCache ) {
        CacheHandler cacheHandler = new CacheHandler(fighterDao, fighterCache);
        return cacheHandler;
    }


    ///////////////////////////
    ///// Quartz
    //////////////
    @Bean
    JobDetail jobDetail(CacheHandler cacheHandler) throws Exception {
        MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
        job.setTargetObject(cacheHandler);
        job.setTargetMethod("refresh");
        job.setName("refresh_cache");
        job.setConcurrent(false);

        job.afterPropertiesSet();


        return job.getObject();
    }

    @Bean
    Trigger simpleTrigger(JobDetail jobDetail) throws ParseException {

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setStartDelay(0);
        trigger.setName("On start");
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setRepeatInterval(Duration.ofDays(1).toMillis());
        trigger.afterPropertiesSet();

        return trigger.getObject();
    }


    @Bean
    Scheduler schedulerFactory(Trigger simpleTrigger, JobDetail jobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(jobDetail);
        scheduler.setTriggers(simpleTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CalculatorConfiguration.class);

    }
}
