package edu.uclm.esi.iso2.payments.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST client for users-service. Provides minimal operations needed by payments-service.
 */
@Component
public class UsersClient {

    private final WebClient webClient;

    public UsersClient(WebClient.Builder webClientBuilder, @Value("${users.service.url:http://localhost:8081}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Map<?, ?> getUsuarioById(Long id) {
        return webClient.get()
                .uri("/api/usuarios/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public boolean comprobarCredito(Long id, double cantidad) {
        Mono<Boolean> mono = webClient.get()
                .uri("/api/usuarios/comprobar-credito/{id}/{cantidad}", id, cantidad)
                .retrieve()
                .bodyToMono(Boolean.class);
        return Boolean.TRUE.equals(mono.block());
    }

    public void actualizarCredito(Long id, double nuevoCredito) {
        webClient.put()
                .uri("/api/usuarios/actualizar-credito/{id}/{nuevoCredito}", id, nuevoCredito)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
