package edu.uclm.esi.iso2.payments.domain.payments.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.uclm.esi.iso2.payments.client.CircuitsClient;
import edu.uclm.esi.iso2.payments.client.UsersClient;
import edu.uclm.esi.iso2.payments.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PagoService {

    private final CircuitsClient circuitsClient;
    private final UsersClient usersClient;
    private final ConcurrentHashMap<String, Map<String, Object>> pagosMem = new ConcurrentHashMap<>();

    public PagoService(CircuitsClient circuitsClient,
                       UsersClient usersClient,
                       @Value("${stripe.secretKey}") String stripeSecretKey) {
        this.circuitsClient = circuitsClient;
        this.usersClient = usersClient;
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Crea un PaymentIntent en Stripe y, en modo test, descuenta el crédito inmediatamente.
     */
    public Map<String, Object> procesarPago(Long usuarioId, Long circuitoId, String metodoPago) {
        var usuario = usersClient.getUsuarioById(usuarioId);
        var circuito = circuitsClient.getCircuitoById(circuitoId);
        double coste = Double.parseDouble(circuito.get("coste").toString());

        // comprobar crédito
        if (!usersClient.comprobarCredito(usuarioId, coste)) {
            return Map.of("success", false, "message", "Crédito insuficiente");
        }

        long amountCents = Math.round(coste * 100);
        String referencia = UUID.randomUUID().toString();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency("eur")
                    .setDescription("Pago circuito " + circuitoId)
                    .putMetadata("referencia", referencia)
                    .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            if ("succeeded".equals(intent.getStatus())) {
                double nuevoCredito = Double.parseDouble(usuario.get("credito").toString()) - coste;
                usersClient.actualizarCredito(usuarioId, nuevoCredito);
            }

            Map<String, Object> info = Map.of(
                    "referencia", referencia,
                    "usuarioId", usuarioId,
                    "circuitoId", circuitoId,
                    "monto", coste,
                    "estado", intent.getStatus(),
                    "stripeId", intent.getId(),
                    "fecha", LocalDateTime.now().toString()
            );
            pagosMem.put(referencia, info);

            return Map.of(
                    "success", true,
                    "estado", intent.getStatus(),
                    "referencia", referencia,
                    "clientSecret", intent.getClientSecret()
            );
        } catch (StripeException e) {
            return Map.of("success", false, "message", "Stripe error: " + e.getMessage());
        }
    }

    public Map<String, Object> verificarEstadoPago(String referencia) {
        Map<String, Object> pago = pagosMem.get(referencia);
        if (pago == null) {
            throw new ResourceNotFoundException("Pago no encontrado: " + referencia);
        }
        try {
            PaymentIntent intent = PaymentIntent.retrieve(pago.get("stripeId").toString());
            pago.put("estado", intent.getStatus());
            return Map.of("success", true, "estado", intent.getStatus(), "referencia", referencia);
        } catch (StripeException e) {
            return Map.of("success", false, "message", "Error Stripe: " + e.getMessage());
        }
    }
}