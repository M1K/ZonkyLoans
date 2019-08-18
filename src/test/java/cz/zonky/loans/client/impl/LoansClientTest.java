package cz.zonky.loans.client.impl;

import cz.zonky.loans.client.pojo.Loan;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * @author Michal Svarc
 */
public class LoansClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansClientTest.class);
    private static LoansClientImpl loansClient;

    @BeforeClass
    public static void setUp() {
        // Whole Spring container is not needed, this is enough for testing. Url has to be injected manually
        loansClient = new LoansClientImpl();
        loansClient.url = "https://api.zonky.cz/loans/marketplace";
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testGetLoans() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date from = calendar.getTime();
        ResponseEntity<Loan[]> response = loansClient.getLoansFrom(from, 10, 0);
        Loan[] loans = response.getBody();
        assertTrue(loans != null && loans.length == 10);
        for (Loan loan : loans) {
            LOGGER.debug("loan: {}", loan);
        }
    }
}
