package com.cely.report_ms.repositories;

import com.cely.report_ms.models.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Repository
@Slf4j
public class CompaniesFallbackRepository {

    private final WebClient webClient;
    private final String uri;

    public CompaniesFallbackRepository(WebClient.Builder webClientBuilder, @Value("${fallback.uri}") String uri) {
        this.webClient = webClientBuilder.build();
        this.uri = uri;
    }

    public Company getByName(String name){
        log.warn("Calling companies fallback {}", uri);
        try {
            return this.webClient
                    .get()
                    .uri(uri, name)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Company.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found: " + name, ex);
        }
    }
}
