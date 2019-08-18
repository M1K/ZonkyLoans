package cz.zonky.loans.client.impl;

import cz.zonky.loans.client.LoansClient;
import cz.zonky.loans.client.pojo.Loan;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

/**
 * @author Michal Svarc
 */
public class LoansClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansClientTest.class);
    private static LoansClient loansClient;

    @BeforeClass
    public static void setUp() {
        loansClient = new LoansClientImpl(); // Whole Spring container is not needed, this is enough for testing
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testGetLoans() throws IOException {
        Loan[] loans = loansClient.getLoansFrom(LocalDateTime.now().minusYears(1L), 10, 0);
        assertTrue(loans != null && loans.length == 10);
        for (Loan loan : loans) {
            LOGGER.debug("loan: {}", loan);
        }
    }
}
