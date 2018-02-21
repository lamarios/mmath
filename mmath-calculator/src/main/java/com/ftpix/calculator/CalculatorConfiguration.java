package com.ftpix.calculator;

import com.ftpix.calculator.web.WebServer;
import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.OrientDBDao;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.j256.ormlite.dao.Dao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

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
    BetterThan betterThan(Dao<MmathFight, Long> fightDao, Dao<MmathFighter, String> fighterDao, OrientDBDao orientDBDao) {
        return new BetterThan(fightDao, fighterDao, orientDBDao);
    }

    @Bean
    WebServer webServer(BetterThan betterThan, Dao<MmathFighter, String> fighterDao) {
        WebServer server = new WebServer(betterThan, port, fighterDao);
        server.setupServer();

        return server;
    }


    ///////////////////////////
    ///// Quartz
    //////////////
    /*
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
*/

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CalculatorConfiguration.class);

    }
}
