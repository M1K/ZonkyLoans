package cz.zonky.loans;

import cz.zonky.loans.job.LoansUpdaterJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/*
    Zkuste naprogramovat kód, který bude každých 5 minut kontrolovat nové půjčky na Zonky tržišti a vypíše je.
    Programové API Zonky tržiště je dostupné na adrese https://api.zonky.cz/loans/marketplace, dokumentace na https://zonky.docs.apiary.io/#
    Výběr technologie necháme na Vás, podmínkou je však Java.
    Při hodnocení úkolu budeme přihlížet k dobré testovatelnosti a čistotě kódu. Předem přiznáváme, že nemusíme over engineered řešení.
 */
@SpringBootApplication
public class LoansApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansApplication.class);
    private static final String GROUP_NAME = "ZonkyLoans";
    private Scheduler scheduler;
    @Value("zonky.loans.job.cron")
    private String loansJobCron;

    public LoansApplication(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(LoansApplication.class, args);
    }

    @PostConstruct
    public void init() {
        this.scheduleJob(LoansUpdaterJob.class, loansJobCron);
    }

    protected void scheduleJob(Class<? extends Job> clazz, String cron) {
        try {
            JobDetail detail = createJobDetail(clazz);
            Trigger trigger = createTrigger(clazz, cron, detail);
            scheduler.addJob(detail, true);
            if (scheduler.checkExists(trigger.getKey())) {
                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
                scheduler.scheduleJob(trigger);
            }
            LOGGER.info("Scheduled job: {}, cron: {}", clazz.getSimpleName(), cron);
        } catch (SchedulerException e) {
            LOGGER.error(null, e);
        }
    }

    private static Trigger createTrigger(Class<? extends Job> clazz, String cron, JobDetail detail) {
        return TriggerBuilder.newTrigger()
                .forJob(detail)
                .withIdentity(clazz.getSimpleName(), GROUP_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
    }

    private static JobDetail createJobDetail(Class<? extends Job> clazz) {
        return JobBuilder.newJob(clazz)
                .withIdentity(clazz.getSimpleName(), GROUP_NAME)
                .storeDurably()
                .build();
    }
}
