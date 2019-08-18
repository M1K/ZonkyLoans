package cz.zonky.loans.client;

import cz.zonky.loans.client.pojo.Loan;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Michal Svarc
 */
public interface LoansClient {

    Integer DEFAULT_PAGE_SIZE = 20;

    Loan[] getLoansFrom(LocalDateTime from, Integer size, Integer page) throws IOException;

    default Loan[] getLoansFrom(LocalDateTime from, Integer page) throws IOException {
        return this.getLoansFrom(from, DEFAULT_PAGE_SIZE, page);
    }
}
