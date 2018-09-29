package com.ftpix.mmath.cron;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cron.graph.GraphGenerator;
import com.ftpix.mmath.cron.stats.StatsRefresher;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.mq.MqConfiguration;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;

@Configuration
@PropertySource("classpath:config.properties")
@Import({DaoConfiguration.class, MqConfiguration.class})
public class CronConfiguration {

    @Bean
    Refresh refresh(JmsTemplate jmsTemplate, MySQLDao dao, String fighterTopic, String eventTopic, String organizationTopic) {
        return new Refresh(jmsTemplate, dao, fighterTopic, eventTopic, organizationTopic);
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
        scheduler.setTriggers(graphCronTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }


    @Bean
    WebController web(Refresh refresh, GraphGenerator graphGenerator, StatsRefresher statsRefresher) {
        return new WebController(refresh, graphGenerator, statsRefresher);

    }

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CronConfiguration.class);

    }


    /////////////////// STATS PROCESSOR
    @Bean
    StatsRefresher statsRefresher(MySQLDao dao) {
        return new StatsRefresher(dao);
    }


    @Bean
    JobDetail statsJobDetail(StatsRefresher statsRefresher) throws Exception {
        MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
        job.setTargetObject(statsRefresher);
        job.setTargetMethod("process");
        job.setName("refresh_stats");
        job.setConcurrent(false);

        job.afterPropertiesSet();

        return job.getObject();
    }

    @Bean
    Trigger statsCronTrigger(JobDetail statsJobDetail) throws ParseException {

        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(statsJobDetail);
        trigger.setCronExpression("0 0 20 ? * TUE");
//         trigger.setCronExpression("00 * * * * ?");
        trigger.setName("weekly_graph");

        trigger.afterPropertiesSet();

        return trigger.getObject();
    }

    @Bean
    Scheduler statsSchedulerFactory(Trigger statsCronTrigger, JobDetail statsJobDetail) throws Exception {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setJobDetails(statsJobDetail);
        scheduler.setTriggers(statsCronTrigger);
        scheduler.setAutoStartup(true);


        scheduler.afterPropertiesSet();

        Scheduler result = scheduler.getObject();
        result.start();
        return result;
    }

}
