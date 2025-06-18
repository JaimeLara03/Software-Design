package edu.uclm.esi.iso2.circuits.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.iso2.circuits.domain.circuits.service.CircuitoService;
import edu.uclm.esi.iso2.circuits.service.CircuitService;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("circuits")
public class CircuitController {

    @Autowired
    private CircuitService service;

    @Autowired
    private CircuitoService circuitoService;

    @GetMapping("/createCircuit")
    public Map<String, Object> createCircuit(@RequestParam int qubits) {
        if (qubits < 1)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The number of qubits must be greater than 0");

       return this.service.createCircuit(qubits);
    }

    @GetMapping("/retrieveCircuit/{id}")
    public ResponseEntity<Map<String, Object>> retrieveCircuit(@PathVariable Long id) {
        try {
            // Verificar si el circuito existe
            circuitoService.getCircuitoById(id);

            // Recuperar el circuito usando el service
            Map<String, Object> circuitData = service.retrieveCircuit(id);
            return new ResponseEntity<>(circuitData, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "No se pudo recuperar el circuito: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkCredit/{userId}/{circuitId}")
    public ResponseEntity<Map<String, Object>> checkCredit(
            @PathVariable Long userId,
            @PathVariable Long circuitId) {
        try {
            // Esta función debería verificar si el usuario tiene suficiente crédito
            boolean hasCredit = circuitoService.verificarDisponibilidadCredito(
                circuitId,
                service.getUserCredit(userId)
            );

            Map<String, Object> response = new HashMap<>();
            response.put("hasCredit", hasCredit);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al verificar crédito: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
