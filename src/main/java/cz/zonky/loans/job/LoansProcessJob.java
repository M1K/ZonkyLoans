package cz.zonky.loans.job;

import cz.zonky.loans.client.LoansClient;
import cz.zonky.loans.client.pojo.Loan;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Michal Svarc
 */
@Component
public class LoansProcessJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansProcessJob.class);
    private static final Integer JOB_INTERVAL_MINUTE = 5; // In minutes
    private LoansClient loansClient;

    public LoansProcessJob(LoansClient loansClient) {
        this.loansClient = loansClient;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Date from = this.getDateFrom(context);
            // Get first page of loans and check how many loans should be processed
            Integer page = 0;
            ResponseEntity<Loan[]> response = loansClient.getLoansFrom(from, page);
            Long toProcess = this.getToProcessTotal(response);
            Long processed = 0L;
            Loan[] loans;
            while ((loans = response.getBody()) != null && loans.length > 0) {
                LOGGER.debug("Found loans: {}", loans.length);
                for (Loan loan : loans) {
                    // Some business logic, store it maybe ...
                }
                processed += loans.length;
                response = loansClient.getLoansFrom(from, ++page);
            }
            LOGGER.info("Processed total loans: {} / {}", processed, toProcess);
            // Maybe some validation?
            if (toProcess == null) {
                // Missing or unknown total, not sure if all was processed
            } else if (toProcess.equals(processed)) {
                // All done
            } else {
                // Not all finished, some status update, raise error?
            }
        } catch (IOException e) {
            LOGGER.error(null, e);
        }
    }

    /*
    Here I would ask for clarification - what is the purpose? Just somehow notify about new loans or process them all? At first run, I have to decide if I should
    process loans just from last 5 mins or all of them. I think the first is correct...
    I am taking from as time when job last ran, so in case of missing run, error etc. I should get missing data.
     */
    private Date getDateFrom(JobExecutionContext context) {
        if (context == null || context.getPreviousFireTime() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -JOB_INTERVAL_MINUTE);
            return calendar.getTime();
        } else {
            return context.getPreviousFireTime();
        }
    }

    private Long getToProcessTotal(ResponseEntity<Loan[]> response) {
        List<String> headerValues = response.getHeaders().get("X-Total");
        if (headerValues == null || headerValues.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(headerValues.get(0));
        } catch (NumberFormatException e) {
            LOGGER.error("Can't parse: {}, error: {}", headerValues.get(0), e.getMessage());
        }
        return null;
    }
}
