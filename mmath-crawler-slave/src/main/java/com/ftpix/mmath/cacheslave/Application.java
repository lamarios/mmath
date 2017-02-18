package com.ftpix.mmath.cacheslave;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.EventDao;
import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.dao.OrganizationDao;
import com.ftpix.sherdogparser.Sherdog;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
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
    Receiver receiver(FighterDao fighterDao, EventDao eventDao, OrganizationDao organizationDao, Sherdog sherdog) {
        return new Receiver(fighterDao, eventDao, organizationDao, sherdog);
    }


    @Bean
    Refresh refresh(Receiver receiver, FighterDao fighterDao) {
        return new Refresh(receiver, fighterDao);
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


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);

    }
}
