package cz.zonky.loans.client.impl;

import cz.zonky.loans.client.LoansClient;
import cz.zonky.loans.client.pojo.Loan;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;

/**
 * @author Michal Svarc
 */
@Service
public class LoansClientImpl implements LoansClient {

    static final FastDateFormat DATE_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoansClientImpl.class);
    @Value("${zonky.loans.job.url}")
    String url;

    @Override
    public ResponseEntity<Loan[]> getLoansFrom(Date from, Integer size, Integer page) throws IOException {
        // Build url
        String fromString = DATE_FORMAT.format(from);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("datePublished__gt", fromString);
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
            throw new IOException("Error, uri: " + uri + " >> response: " + response.getStatusCode());
        } else if (response.getBody() == null) {
            LOGGER.warn("No content, uri: " + uri);
            return null;
        }
        return response;
    }
}
