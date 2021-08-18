package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AlertRabbit {

    private static Properties properties;

    private static void init() {
        properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
            Class.forName(properties.getProperty("jdbc.driver"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password")
        )) {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            data.put("connection", connection);
            JobDetail job = JobBuilder.newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(properties.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            Thread.sleep(1000);
            System.out.println(store);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            long time = System.currentTimeMillis();
            store.add(time);
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection
                    .prepareStatement("insert into rabbit (created_date) values (?)")) {
                statement.setTimestamp(1, new Timestamp(time));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
