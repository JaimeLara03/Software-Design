package edu.uclm.esi.circuits.domain.payments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.circuits.domain.circuits.model.Circuito;
import edu.uclm.esi.circuits.domain.circuits.service.CircuitoService;
import edu.uclm.esi.circuits.domain.payments.model.Pago;
import edu.uclm.esi.circuits.domain.payments.repository.PagoRepository;
import edu.uclm.esi.circuits.domain.users.model.Usuario;
import edu.uclm.esi.circuits.domain.users.service.UsuarioService;
import edu.uclm.esi.circuits.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private CircuitoService circuitoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PaymentServiceClient paymentServiceClient;
    
    public List<Pago> getAllPagos() {
        return pagoRepository.findAll();
    }
    
    public Pago getPagoById(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
    }
    
    public List<Pago> getPagosByUsuarioId(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId);
    }
    
    public List<Pago> getPagosByCircuitoId(Long circuitoId) {
        return pagoRepository.findByCircuitoId(circuitoId);
    }
    
    @Transactional
    public Map<String, Object> procesarPago(Long usuarioId, Long circuitoId, String metodoPago) {
        // Verificar que usuario y circuito existan
        Usuario usuario = usuarioService.getUsuarioById(usuarioId);
        Circuito circuito = circuitoService.getCircuitoById(circuitoId);
        
        // Verificar crédito disponible
        if (!usuarioService.comprobarCredito(usuarioId, circuito.getCoste())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Crédito insuficiente para realizar el pago");
            return response;
        }
        
        // Crear registro de pago
        Pago pago = new Pago();
        pago.setUsuarioId(usuarioId);
        pago.setCircuitoId(circuitoId);
        pago.setMonto(circuito.getCoste());
        pago.setReferenciaPago(UUID.randomUUID().toString());
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstado("PENDIENTE");
        pago.setMetodoPago(metodoPago);
        
        pagoRepository.save(pago);
        
        // Preparar datos para el servicio de pago externo
        Map<String, Object> datosPago = new HashMap<>();
        datosPago.put("referenciaPago", pago.getReferenciaPago());
        datosPago.put("monto", pago.getMonto());
        datosPago.put("metodoPago", metodoPago);
        datosPago.put("usuarioId", usuarioId);
        datosPago.put("usuarioEmail", usuario.getEmail());
        
        // Llamar al servicio de pago externo
        Map<String, Object> resultadoPago = paymentServiceClient.procesarPago(datosPago);
        
        // Actualizar estado del pago según la respuesta
        if (resultadoPago.get("success") != null && (Boolean) resultadoPago.get("success")) {
            pago.setEstado("COMPLETADO");
            
            // Actualizar crédito del usuario
            double nuevoCredito = usuario.getCredito() - circuito.getCoste();
            usuarioService.actualizarCredito(usuarioId, nuevoCredito);
            
            pagoRepository.save(pago);
            
            resultadoPago.put("pagoId", pago.getId());
        } else {
            pago.setEstado("FALLIDO");
            pagoRepository.save(pago);
        }
        
        return resultadoPago;
    }
    
    @Transactional
    public Map<String, Object> verificarEstadoPago(String referenciaPago) {
        Pago pago = pagoRepository.findByReferenciaPago(referenciaPago)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con referencia: " + referenciaPago));
        
        // Si el pago ya está completado o fallido, devolver el estado actual
        if ("COMPLETADO".equals(pago.getEstado()) || "FALLIDO".equals(pago.getEstado())) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("success", "COMPLETADO".equals(pago.getEstado()));
            resultado.put("estado", pago.getEstado());
            resultado.put("fechaPago", pago.getFechaPago());
            resultado.put("referenciaPago", pago.getReferenciaPago());
            return resultado;
        }
        
        // Si está pendiente, verificar con el servicio externo
        Map<String, Object> estadoPago = paymentServiceClient.verificarPago(referenciaPago);
        
        // Actualizar estado según la respuesta
        if (estadoPago.get("success") != null && (Boolean) estadoPago.get("success")) {
            if (estadoPago.get("estado") != null) {
                pago.setEstado((String) estadoPago.get("estado"));
                
                // Si el pago se completa, actualizar crédito del usuario
                if ("COMPLETADO".equals(pago.getEstado())) {
                    Usuario usuario = usuarioService.getUsuarioById(pago.getUsuarioId());
                    Circuito circuito = circuitoService.getCircuitoById(pago.getCircuitoId());
                    
                    double nuevoCredito = usuario.getCredito() - circuito.getCoste();
                    usuarioService.actualizarCredito(pago.getUsuarioId(), nuevoCredito);
                }
                
                pagoRepository.save(pago);
            }
        }
        
        return estadoPago;
    }
}
