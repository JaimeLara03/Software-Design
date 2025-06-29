package edu.uclm.esi.iso2.payments.application.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import edu.uclm.esi.iso2.payments.application.dto.PaymentRequest;
import edu.uclm.esi.iso2.payments.domain.model.Pago;
import edu.uclm.esi.iso2.payments.domain.service.PagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Pago>> getPagosByUserId(@PathVariable String userId) {
        List<Pago> pagos = pagoService.getPagosByUserId(userId);
        return ResponseEntity.ok(pagos);
    }

    @PostMapping("/crear-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentIntent paymentIntent = pagoService.crearPaymentIntent(paymentRequest.getUsuarioId(), paymentRequest.getMonto());
            return ResponseEntity.ok(Collections.singletonMap("clientSecret", paymentIntent.getClientSecret()));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            logger.error("Stripe webhook secret is not configured.");
            return new ResponseEntity<>("Webhook secret not configured.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sigHeader == null) {
            return new ResponseEntity<>("Stripe-Signature header missing", HttpStatus.BAD_REQUEST);
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            logger.warn("Stripe webhook signature verification failed.", e);
            return new ResponseEntity<>("Signature verification failed.", HttpStatus.BAD_REQUEST);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
             return new ResponseEntity<>("Event data deserialization failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                logger.info("PaymentIntent {} succeeded. User: {}", paymentIntent.getId(), paymentIntent.getMetadata().get("userId"));
                try {
                    pagoService.handleSuccessfulPayment(paymentIntent);
                } catch (Exception e) {
                    logger.error("Error handling successful payment for intent {}", paymentIntent.getId(), e);
                    return new ResponseEntity<>("Error handling payment.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            default:
                logger.warn("Unhandled event type: {}", event.getType());
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
