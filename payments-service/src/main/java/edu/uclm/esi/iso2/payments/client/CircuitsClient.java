package edu.uclm.esi.iso2.payments.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST client for circuits-service.
 */
@Component
public class CircuitsClient {

    private final WebClient webClient;

    public CircuitsClient(WebClient.Builder webClientBuilder, @Value("${circuits.service.url:http://localhost:8082}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Map<?, ?> getCircuitoById(Long id) {
        return webClient.get()
                .uri("/api/circuitos/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public boolean verificarCredito(Long circuitoId, double credito) {
        Mono<Boolean> mono = webClient.get()
                .uri("/api/circuitos/verificarcredito/{circuitoId}/{credito}", circuitoId, credito)
                .retrieve()
                .bodyToMono(Boolean.class);
        return Boolean.TRUE.equals(mono.block());
    }
}
