package edu.uclm.esi.circuits.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uclm.esi.circuits.domain.circuits.model.Circuito;
import edu.uclm.esi.circuits.domain.circuits.service.CircuitoService;
import edu.uclm.esi.circuits.domain.users.model.Usuario;
import edu.uclm.esi.circuits.domain.users.service.UsuarioService;
import edu.uclm.esi.circuits.exception.ResourceNotFoundException;

@Service
public class CircuitService {
    
    @Autowired
    private CircuitoService circuitoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    public Map<String, Object> createCircuit(int qubits) {
        Map<String, Object> circuit = new HashMap<>();
        circuit.put("qubits", qubits);
        
        // Si el circuito tiene más de 6 qubits, verificar crédito
        boolean needsCredit = qubits > 6;
        double cost = needsCredit ? calculateCost(qubits) : 0.0;
        
        // Crear y guardar el circuito en la base de datos
        Circuito circuito = new Circuito();
        circuito.setNombre("Circuito Cuántico " + qubits + "q");
        circuito.setDescripcion("Circuito cuántico de " + qubits + " qubits generado automáticamente");
        circuito.setUbicacion("Simulador local");
        circuito.setCoste(cost);
        circuito.setActivo(true);
        
        // Guardar el circuito en la base de datos
        Circuito savedCircuito = circuitoService.createCircuito(circuito);
        
        // Añadir propiedades específicas del circuito cuántico
        circuit.put("id", savedCircuito.getId());
        circuit.put("gates", new HashMap<String, Object>());
        circuit.put("needsCredit", needsCredit);
        circuit.put("cost", cost);
        
        return circuit;
    }
    
    public Map<String, Object> retrieveCircuit(Long id) {
        try {
            // Recuperar datos del circuito de la base de datos
            Circuito circuito = circuitoService.getCircuitoById(id);
            
            // Convertir a formato adecuado para el frontend
            Map<String, Object> circuitData = new HashMap<>();
            circuitData.put("id", circuito.getId());
            circuitData.put("nombre", circuito.getNombre());
            circuitData.put("descripcion", circuito.getDescripcion());
            circuitData.put("ubicacion", circuito.getUbicacion());
            circuitData.put("coste", circuito.getCoste());
            
            // Estimar qubits basado en el nombre si contiene formato "Circuito Cuántico Xq"
            int estimatedQubits = 1;
            if (circuito.getNombre().matches("Circuito Cuántico \\d+q")) {
                String qubitsStr = circuito.getNombre().replaceAll("\\D+", "");
                try {
                    estimatedQubits = Integer.parseInt(qubitsStr);
                } catch (NumberFormatException e) {
                    // Si hay un error al parsear, usar la estimación basada en coste
                    estimatedQubits = Math.max(1, (int)(circuito.getCoste() / 5.0) + 6);
                }
            } else {
                // Estimar qubits basado en coste como fallback
                estimatedQubits = Math.max(1, (int)(circuito.getCoste() / 5.0) + 6);
            }
            
            circuitData.put("qubits", estimatedQubits);
            
            // Añadir información de las puertas del circuito (simulado)
            Map<String, Object> gates = new HashMap<>();
            gates.put("hadamard", estimatedQubits);
            gates.put("cnot", Math.max(0, estimatedQubits - 1));
            gates.put("measurement", estimatedQubits);
            circuitData.put("gates", gates);
            
            // Propiedades adicionales para compatibilidad con la API anterior
            circuitData.put("needsCredit", circuito.getCoste() > 0);
            circuitData.put("cost", circuito.getCoste());
            
            return circuitData;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error al recuperar el circuito: " + e.getMessage(), e);
        }
    }
    
    public double getUserCredit(Long userId) {
        try {
            Usuario usuario = usuarioService.getUsuarioById(userId);
            return usuario.getCredito();
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener crédito del usuario: " + e.getMessage(), e);
        }
    }
    
    private double calculateCost(int qubits) {
        // Fórmula simple: 5 euros por cada qubit por encima de 6
        return (qubits - 6) * 5.0;
    }
}
