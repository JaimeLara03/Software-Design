package edu.uclm.esi.iso2.circuits.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Simple client to interact with users-service via REST.
 */
@Component
public class UsersClient {

    private final WebClient webClient;

    public UsersClient(@Value("${users.service.url:http://localhost:8081}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Retrieve current credit of a user.
     * Calls GET /api/usuarios/{id} and expects a JSON field "credito" in response.
     */
    public double getUserCredit(Long userId) {
        Mono<Map> mono = webClient.get()
                .uri("/api/usuarios/{id}", userId)
                .retrieve()
                .bodyToMono(Map.class);

        Map<?, ?> body = mono.block();
        if (body == null || !body.containsKey("credito")) {
            throw new IllegalStateException("Respuesta del users-service sin campo 'credito'");
        }
        Object value = body.get("credito");
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Valor de credito no válido: " + value);
        }
    }
}
