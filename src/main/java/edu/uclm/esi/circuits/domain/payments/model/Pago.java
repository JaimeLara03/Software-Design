package edu.uclm.esi.circuits.domain.payments.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long usuarioId;
    
    private Long circuitoId;
    
    private double monto;
    
    private String referenciaPago;
    
    private LocalDateTime fechaPago;
    
    private String estado; // PENDIENTE, COMPLETADO, FALLIDO
    
    private String metodoPago;
}
