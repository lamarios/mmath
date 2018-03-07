package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cacheslave.graph.GraphGenerator;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.sherdogparser.Sherdog;
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
    Receiver receiver(MySQLDao dao, Sherdog sherdog) {
        return new Receiver(dao, sherdog);
    }


    @Bean
    Refresh refresh(Receiver receiver, MySQLDao dao) {
        return new Refresh(receiver, dao);
    }

    @Bean
    GraphGenerator graphGenerator(OrientDBDao orientDBDao, MySQLDao dao) {
        return new GraphGenerator(orientDBDao, dao);
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
        trigger.setCronExpression("0 0 0 ? * TUE");
        // trigger.setCronExpression("00 * * * * ?");
        trigger.setName("weekly");

        trigger.afterPropertiesSet();

        return trigger.getObject();
    }


    @Bean
    Scheduler schedulerFactory(Trigger cronTrigger,  JobDetail jobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(jobDetail);
        scheduler.setTriggers(cronTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }

    ///////////////////GRAPH JOBS

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
        trigger.setCronExpression("0 0 23 ? * TUE");
//         trigger.setCronExpression("00 * * * * ?");
        trigger.setName("weekly_graph");

        trigger.afterPropertiesSet();

        return trigger.getObject();
    }

    @Bean
    Scheduler graphSchedulerFactory(Trigger graphCronTrigger, JobDetail graphJobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(graphJobDetail);
        scheduler.setTriggers( graphCronTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }


    @Bean
    WebController web(Refresh refresh, GraphGenerator graphGenerator){
        return new WebController(refresh, graphGenerator);

    }

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);

    }
}
