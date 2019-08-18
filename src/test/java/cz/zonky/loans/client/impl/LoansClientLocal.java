package cz.zonky.loans.client.impl;

import cz.zonky.loans.client.LoansClient;
import cz.zonky.loans.client.pojo.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static cz.zonky.loans.client.impl.LoansClientImpl.DATE_FORMAT;

/**
 * @author Michal Svarc
 * Local dev testing
 */
public class LoansClientLocal implements LoansClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansClientLocal.class);
    private static final String BASE_URL = "https://api.zonky.cz/loans/marketplace";

    public static void main(String[] args) {
        try {
            String fromString = "2019-08-15T00:00:00.000+02:00";
            Date from = DATE_FORMAT.parse(fromString);
//            Date from = new Date();
            ResponseEntity<Loan[]> response = new LoansClientLocal().getLoansFrom(from, 10, 0);
            List<String> headers = response.getHeaders().get("X-Total");
            Long total = headers == null || headers.isEmpty() ? null : Long.parseLong(headers.get(0));
            LOGGER.debug("Total to process: {}", total);
            Loan[] loans = response.getBody();
            if (loans != null) {
                LOGGER.debug("Returned loans: {}", loans.length);
                for (Loan loan : loans) {
                    LOGGER.debug("{}\n", loan);
                }
            }
        } catch (Exception e) {
            LOGGER.error(null, e);
        }
    }

    @Override
    public ResponseEntity<Loan[]> getLoansFrom(Date from, Integer size, Integer page) throws IOException {
        // Build url
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("datePublished__gt", DATE_FORMAT.format(from));
        URI uri = builder.build().toUri();
        LOGGER.debug("Computed url: {}", uri);
        // Rest template
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Size", size.toString());
        headers.set("X-Page", page.toString());
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        // Get response
        ResponseEntity<Loan[]> response = template.exchange(uri, HttpMethod.GET, entity, Loan[].class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Error, response: " + response);
        } else if (response.getBody() == null) {
            LOGGER.warn("Nothing returned - response: {}", response);
            return null;
        }
        return response;
    }
}
