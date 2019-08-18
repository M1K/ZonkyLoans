package cz.zonky.loans.job;

import cz.zonky.loans.AbstractSpringTest;
import cz.zonky.loans.LoansApplication;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Michal Svarc
 */
public class LoansProcessJobTest extends AbstractSpringTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansProcessJobTest.class);
    @Autowired
    private LoansApplication loansApplication;

    @Test
    public void testRunJob() {
        loansApplication.startJob(LoansProcessJob.class);
    }
}
