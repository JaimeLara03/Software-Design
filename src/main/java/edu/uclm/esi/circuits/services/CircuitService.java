package edu.uclm.esi.circuits.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CircuitService {
public Map<String, Object> createCircuit(int qubits) {

        Map<String, Object> circuit = new HashMap<>();

        circuit.put("qubits", qubits);

        // Add more logic to create the circuit as needed

        return circuit;

    }
}
