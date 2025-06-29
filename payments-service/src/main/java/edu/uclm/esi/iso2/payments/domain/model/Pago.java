package edu.uclm.esi.iso2.payments.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String stripePaymentId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    public Pago(String stripePaymentId, String userId, BigDecimal amount, String currency) {
        this.stripePaymentId = stripePaymentId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentDate = LocalDateTime.now();
    }
}
