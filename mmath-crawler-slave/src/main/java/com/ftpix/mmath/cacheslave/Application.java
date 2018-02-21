package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cacheslave.graph.GraphGenerator;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.MmathEvent;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.MmathOrganization;
import com.ftpix.sherdogparser.Sherdog;
import com.j256.ormlite.dao.Dao;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.text.ParseException;

/**
 * Created by gz on 16-Sep-16.
 */
@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class})
public class Application {
    private final static int CORE_POOL_SIZE = 1, MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 15, THREAD_TIMEOUT = 60;


    //Thread pools


    //Receiver
    @Bean
    Receiver receiver(Dao<MmathFighter, String> fighterDao, Dao<MmathEvent, String> eventDao, Dao<MmathOrganization, String> orgDao, Dao<MmathFight, Long> fightDao, Sherdog sherdog) {
        return new Receiver(fighterDao, eventDao, orgDao, fightDao, sherdog);
    }


    @Bean
    Refresh refresh(Receiver receiver, Dao<MmathFighter, String> fighterDao) {
        return new Refresh(receiver, fighterDao);
    }

    @Bean
    GraphGenerator graphGenerator(OrientDBDao orientDBDao, Dao<MmathFight, Long> fightDao) {
        return new GraphGenerator(orientDBDao, fightDao);
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
        trigger.setCronExpression("00 00 00 * * TUE");
        // trigger.setCronExpression("00 * * * * ?");
        trigger.setName("weekly");

        trigger.afterPropertiesSet();

        return trigger.getObject();
    }

    @Bean
    Trigger simpleTrigger(JobDetail jobDetail) throws ParseException {

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setStartDelay(0);
        trigger.setName("On start");
        trigger.setRepeatCount(0);
        trigger.setRepeatInterval(1);
        trigger.afterPropertiesSet();

        return trigger.getObject();
    }


    @Bean
    Scheduler schedulerFactory(Trigger cronTrigger, Trigger simpleTrigger, JobDetail jobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(jobDetail);
        scheduler.setTriggers(cronTrigger, simpleTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }

    ///////////////////GRAPH JOBS
    @Bean
    Trigger graphSimpleTrigger(JobDetail graphJobDetail) throws ParseException {

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(graphJobDetail);
        trigger.setStartDelay(0);
        trigger.setName("On start graph");
        trigger.setRepeatCount(0);
        trigger.setRepeatInterval(1);
        trigger.afterPropertiesSet();

        return trigger.getObject();
    }

    @Bean
    JobDetail graphJobDetail(GraphGenerator graphGenerator) throws Exception {
        MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
        job.setTargetObject(graphGenerator);
        job.setTargetMethod("process");
        job.setName("refresh_graph");
        job.setConcurrent(false);

        job.afterPropertiesSet();

        return job.getObject();
    }

    @Bean
    Trigger graphCronTrigger(JobDetail graphJobDetail) throws ParseException {

        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(graphJobDetail);
        trigger.setCronExpression("23 00 00 * * TUE");
        // trigger.setCronExpression("00 * * * * ?");
        trigger.setName("weekly_graph");

        trigger.afterPropertiesSet();

        return trigger.getObject();
    }
    @Bean
    Scheduler graphSchedulerFactory(Trigger graphCronTrigger, Trigger graphSimpleTrigger, JobDetail graphJobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(graphJobDetail);
        scheduler.setTriggers(graphSimpleTrigger, graphCronTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);

    }
}
