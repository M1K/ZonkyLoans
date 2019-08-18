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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * @author Michal Svarc
 */
public class LoansClientLocal implements LoansClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansClientLocal.class);
    private static final String BASE_URL = "https://api.zonky.cz/loans/marketplace";

    public static void main(String[] args) {
        try {
            String fromString = "2019-08-15T18:23:53.101+02:00";
            ZonedDateTime from = ZonedDateTime.parse(fromString, DateTimeFormatter.ISO_DATE_TIME);
            Loan[] loans = new LoansClientLocal().getLoansFrom(from.toLocalDateTime(), 10, 0);
            if (loans != null) {
                LOGGER.debug("Returned loans: {}", loans.length);
                for (Loan loan : loans) {
                    LOGGER.debug("{}\n", loan);
                }
            }
        } catch (IOException e) {
            LOGGER.error(null, e);
        }
    }

    @Override
    public Loan[] getLoansFrom(LocalDateTime from, Integer size, Integer page) throws IOException {
        // Build url
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("datePublished", from.toString());
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
        return response.getBody();
    }
}
