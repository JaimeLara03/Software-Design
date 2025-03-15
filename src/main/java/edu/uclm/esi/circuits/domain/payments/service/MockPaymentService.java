package edu.uclm.esi.circuits.domain.payments.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio mock para simular una pasarela de pago externa.
 * Útil para pruebas y desarrollo sin depender de un servicio real.
 */
@Service
public class MockPaymentService {
    
    /**
     * Simula el procesamiento de un pago
     * @param amount Monto del pago
     * @param creditCard Número de tarjeta de crédito
     * @param expirationDate Fecha de expiración de la tarjeta
     * @param cvv Código de seguridad de la tarjeta
     * @return Mapa con resultado de la operación
     */
    public Map<String, Object> procesarPago(double amount, String creditCard, String expirationDate, String cvv) {
        Map<String, Object> resultado = new HashMap<>();
        
        // Validaciones básicas
        if (creditCard == null || creditCard.length() < 16) {
            resultado.put("success", false);
            resultado.put("message", "Número de tarjeta inválido");
            return resultado;
        }
        
        if (expirationDate == null || !expirationDate.matches("\\d{2}/\\d{2}")) {
            resultado.put("success", false);
            resultado.put("message", "Fecha de expiración inválida");
            return resultado;
        }
        
        if (cvv == null || cvv.length() < 3) {
            resultado.put("success", false);
            resultado.put("message", "CVV inválido");
            return resultado;
        }
        
        // Simulamos que la tarjeta termina en "0000" es rechazada
        if (creditCard.endsWith("0000")) {
            resultado.put("success", false);
            resultado.put("message", "Tarjeta rechazada por el banco");
            return resultado;
        }
        
        // Transacción exitosa
        String transactionId = UUID.randomUUID().toString();
        resultado.put("success", true);
        resultado.put("message", "Pago procesado correctamente");
        resultado.put("transactionId", transactionId);
        resultado.put("amount", amount);
        resultado.put("timestamp", System.currentTimeMillis());
        
        return resultado;
    }
    
    /**
     * Simula la verificación de un pago existente
     * @param transactionId ID de la transacción
     * @return Estado de la transacción
     */
    public Map<String, Object> verificarPago(String transactionId) {
        Map<String, Object> resultado = new HashMap<>();
        
        // Para simular, consideramos que transactionId que empiezan con 'a' son fallidos
        if (transactionId.toLowerCase().startsWith("a")) {
            resultado.put("success", false);
            resultado.put("estado", "FALLIDO");
            resultado.put("message", "Transacción rechazada");
        } else {
            resultado.put("success", true);
            resultado.put("estado", "COMPLETADO");
            resultado.put("message", "Transacción completada con éxito");
        }
        
        resultado.put("transactionId", transactionId);
        resultado.put("timestamp", System.currentTimeMillis());
        
        return resultado;
    }
}
