package com.ftpix.mmath.cron;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.cron.graph.GraphGenerator;
import com.ftpix.mmath.cron.hypetrain.HypeTrainStatsGeneration;
import com.ftpix.mmath.cron.stats.StatsRefresher;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.mq.MqConfiguration;
import com.ftpix.mmath.reddit.RedditConfiguration;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@Import({DaoConfiguration.class, MqConfiguration.class, RedditConfiguration.class})
@ComponentScan("com.ftpix.mmath")
@EnableScheduling
public class CronConfiguration {


    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CronConfiguration.class);

    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

}
