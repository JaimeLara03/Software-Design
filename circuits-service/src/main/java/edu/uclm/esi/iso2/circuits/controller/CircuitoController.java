package edu.uclm.esi.iso2.circuits.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import edu.uclm.esi.iso2.circuits.domain.model.Circuito;
import edu.uclm.esi.iso2.circuits.domain.service.CircuitoService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/circuitos")
public class CircuitoController {

    @Autowired
    private CircuitoService circuitoService;

    @GetMapping
    public ResponseEntity<List<Circuito>> getAllCircuitos() {
        List<Circuito> circuitos = circuitoService.getAllCircuitos();
        return new ResponseEntity<>(circuitos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Circuito> getCircuitoById(@PathVariable Long id) {
        Circuito circuito = circuitoService.getCircuitoById(id);
        return new ResponseEntity<>(circuito, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Circuito> createCircuito(@Valid @RequestBody Circuito circuito) {
        Circuito nuevoCircuito = circuitoService.createCircuito(circuito);
        return new ResponseEntity<>(nuevoCircuito, HttpStatus.CREATED);
    }

    
    // Endpoint alternativo para crear circuitos usando un parámetro "qubits" desde el front
    @GetMapping(params = "qubits")
    public ResponseEntity<Circuito> createCircuitoPorQubits(@RequestParam int qubits) {
        Circuito circuito = new Circuito();
        circuito.setNombre("Circuito autogen");
        circuito.setDescripcion("Generado automáticamente");
        circuito.setUbicacion("Desconocida");
        circuito.setQubits(qubits);
        circuito.setCoste(0);
        circuito.setActivo(true);
        Circuito nuevo = circuitoService.createCircuito(circuito);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Circuito> updateCircuito(@PathVariable Long id, @Valid @RequestBody Circuito circuito) {
        Circuito updatedCircuito = circuitoService.updateCircuito(id, circuito);
        return new ResponseEntity<>(updatedCircuito, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCircuito(@PathVariable Long id) {
        circuitoService.deleteCircuito(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/costeminimo/{coste}")
    public ResponseEntity<List<Circuito>> getCircuitosByCosteMinimoOrMayor(@PathVariable double coste) {
        List<Circuito> circuitos = circuitoService.getCircuitosByCosteMinimoOrMayor(coste);
        return new ResponseEntity<>(circuitos, HttpStatus.OK);
    }

    @GetMapping("/verificarcredito/{circuitoId}/{credito}")
    public ResponseEntity<Boolean> verificarDisponibilidadCredito(
            @PathVariable Long circuitoId,
            @PathVariable double credito) {
        boolean disponible = circuitoService.verificarDisponibilidadCredito(circuitoId, credito);
        return new ResponseEntity<>(disponible, HttpStatus.OK);
    }
}
