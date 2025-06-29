package edu.uclm.esi.iso2.payments.domain.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.uclm.esi.iso2.payments.client.CircuitsClient;
import edu.uclm.esi.iso2.payments.client.UsersClient;
import edu.uclm.esi.iso2.payments.domain.model.PagoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import jakarta.annotation.PostConstruct;
import edu.uclm.esi.iso2.payments.domain.model.Pago;

import java.util.List;
import java.util.Map;

@Service
public class PagoService {

    private final CircuitsClient circuitsClient;
    private final UsersClient usersClient;
    private final PagoRepository pagoRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PagoService(CircuitsClient circuitsClient, UsersClient usersClient, PagoRepository pagoRepository) {
        this.circuitsClient = circuitsClient;
        this.usersClient = usersClient;
        this.pagoRepository = pagoRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;  // Configuramos la clave de Stripe tras construcción del bean
    }

    public PaymentIntent crearPaymentIntent(Long usuarioId, double monto) throws StripeException {
        // Validamos que el usuario exista antes de procesar el pago
        usersClient.getUsuarioById(usuarioId);

        long amountCents = Math.round(monto * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency("eur")
                .setDescription("Recarga de crédito para el usuario " + usuarioId)
                .putMetadata("userId", String.valueOf(usuarioId))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        return PaymentIntent.create(params);
    }

    public Map<String, Object> procesarPago(Long usuarioId, double monto, String metodoPago) {
        // Validamos que el usuario exista antes de procesar el pago
        usersClient.getUsuarioById(usuarioId);

        long amountCents = Math.round(monto * 100);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency("eur")
                    .setDescription("Recarga de crédito para el usuario " + usuarioId)
                    .putMetadata("userId", String.valueOf(usuarioId))
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return Map.of(
                    "clientSecret", intent.getClientSecret()
            );
        } catch (StripeException e) {
            throw new RuntimeException("Error processing payment with Stripe: " + e.getMessage(), e);
        }
    }

    public List<Pago> getPagosByUserId(String userId) {
        return pagoRepository.findByUserId(userId);
    }

    @Transactional
    public void handleSuccessfulPayment(PaymentIntent paymentIntent) {
        String stripePaymentId = paymentIntent.getId();
        String userIdString = paymentIntent.getMetadata().get("userId");
        if (userIdString == null) {
            throw new RuntimeException("User ID not found in payment intent metadata");
        }
        Long userId = Long.valueOf(userIdString);
        BigDecimal amount = BigDecimal.valueOf(paymentIntent.getAmount()).divide(new BigDecimal(100));
        String currency = paymentIntent.getCurrency();

        Pago pago = new Pago(stripePaymentId, userIdString, amount, currency);
        pagoRepository.save(pago);

        Map<?, ?> user = usersClient.getUsuarioById(userId);
        if (user != null && user.containsKey("credito")) {
            double currentCredit = ((Number) user.get("credito")).doubleValue();
            double amountToAdd = amount.doubleValue();
            usersClient.actualizarCredito(userId, currentCredit + amountToAdd);
        } else {
            throw new RuntimeException("User not found or credit information is missing for user " + userId);
        }
    }
}
