package edu.uclm.esi.iso2.payments.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(addAuthorizationHeader());
    }

    private ExchangeFilterFunction addAuthorizationHeader() {
        return (clientRequest, next) -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                    ClientRequest filteredRequest = ClientRequest.from(clientRequest)
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                            .build();
                    return next.exchange(filteredRequest);
                }
            }
            return next.exchange(clientRequest);
        };
    }
}
