package cz.zonky.loans.job;

import cz.zonky.loans.client.LoansClient;
import cz.zonky.loans.client.pojo.Loan;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author Michal Svarc
 */
@Component
public class LoansUpdaterJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansUpdaterJob.class);
    private static final String JOB_NAME = LoansUpdaterJob.class.getSimpleName();
    private LoansClient loansClient;

    public LoansUpdaterJob(LoansClient loansClient) {
        this.loansClient = loansClient;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Date previousFireTime = context.getTrigger().getPreviousFireTime();
            LocalDateTime from = previousFireTime == null ?
                    LocalDateTime.now().minusMinutes(5L) :
                    LocalDateTime.ofEpochSecond(previousFireTime.getTime(), 0, ZoneOffset.of("Europe/Prague"));

            Loan[] loans;
            int page = 0;
            int total = 0;
            while ((loans = loansClient.getLoansFrom(from, page)) != null) {
                LOGGER.debug("Found loans: {}", loans.length);
                // Business logic...
                page++;
                total += loans.length;
            }
            LOGGER.info("Processed total loans: {}", total);
        } catch (IOException e) {
            LOGGER.error(null, e);
        }
    }
}
