package edu.uclm.esi.iso2.payments.application.controllers;

import com.google.gson.JsonSyntaxException;
import com.stripe.net.ApiResource;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;

import edu.uclm.esi.iso2.payments.domain.model.Pago;
import edu.uclm.esi.iso2.payments.domain.model.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);



    @Autowired
    private PagoRepository pagoRepository;

    @PostMapping("/events")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = ApiResource.GSON.fromJson(payload, Event.class);
        } catch (JsonSyntaxException e) {
            logger.error("Error parsing JSON: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for details.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to deserialize event data");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                logger.info("Payment for {} succeeded.", paymentIntent.getAmount());
                
                String userId = paymentIntent.getMetadata().get("userId");
                BigDecimal amount = BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100));
                String currency = paymentIntent.getCurrency();
                String stripePaymentId = paymentIntent.getId();

                Pago pago = new Pago(stripePaymentId, userId, amount, currency);
                pagoRepository.save(pago);
                
                break;
            default:
                logger.warn("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok().build();
    }
}
