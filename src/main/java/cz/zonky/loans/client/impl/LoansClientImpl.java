package cz.zonky.loans.client.impl;

import cz.zonky.loans.client.LoansClient;
import cz.zonky.loans.client.pojo.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author Michal Svarc
 */
@Service
public class LoansClientImpl implements LoansClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansClientImpl.class);
    @Value("zonky.loans.job.url")
    private String url;

    @Override
    public Loan[] getLoansFrom(LocalDateTime from, Integer size, Integer page) throws IOException {
        // Build url
        String fromString = from.toString();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("datePublished", fromString);
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
        return response.getBody();
    }
}
