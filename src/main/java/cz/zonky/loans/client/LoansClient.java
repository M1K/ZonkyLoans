package cz.zonky.loans.client;

import cz.zonky.loans.client.pojo.Loan;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Date;

/**
 * @author Michal Svarc
 */
public interface LoansClient {

    Integer DEFAULT_PAGE_SIZE = 20;

    ResponseEntity<Loan[]> getLoansFrom(Date from, Integer size, Integer page) throws IOException;

    default ResponseEntity<Loan[]> getLoansFrom(Date from, Integer page) throws IOException {
        return this.getLoansFrom(from, DEFAULT_PAGE_SIZE, page);
    }
}
