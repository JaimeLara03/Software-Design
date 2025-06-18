package edu.uclm.esi.iso2.payments.domain.payments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceClient {

    @Value("${app.payment.service.url}")
    private String paymentServiceUrl;

    @Value("${app.payment.service.apikey}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public PaymentServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> procesarPago(Map<String, Object> datosPago) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(datosPago, headers);

            return restTemplate.postForObject(paymentServiceUrl + "/procesar", request, Map.class);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al procesar el pago: " + e.getMessage());
            return response;
        }
    }

    public Map<String, Object> verificarPago(String referenciaPago) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> datosPago = new HashMap<>();
            datosPago.put("referenciaPago", referenciaPago);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(datosPago, headers);

            return restTemplate.postForObject(paymentServiceUrl + "/verificar", request, Map.class);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al verificar el pago: " + e.getMessage());
            return response;
        }
    }
}
