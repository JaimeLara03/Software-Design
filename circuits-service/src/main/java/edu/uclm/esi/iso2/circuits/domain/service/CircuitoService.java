package edu.uclm.esi.iso2.circuits.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.iso2.circuits.domain.model.Circuito;
import edu.uclm.esi.iso2.circuits.domain.repository.CircuitoRepository;
import edu.uclm.esi.iso2.circuits.exception.ResourceNotFoundException;
import edu.uclm.esi.iso2.circuits.domain.model.Puerta;

import java.util.List;
import java.util.ArrayList;

@Service
public class CircuitoService {

    @Autowired
    private CircuitoRepository circuitoRepository;

    public List<Circuito> getAllCircuitos() {
        return circuitoRepository.findByActivoTrue();
    }

    public Circuito getCircuitoById(Long id) {
        return circuitoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuito no encontrado con id: " + id));
    }

    @Transactional
    public Circuito createCircuito(Circuito circuito) {
        int q = circuito.getQubits();
        double cost = q > 6 ? (q - 6) * 1.0 : 0.0;
        circuito.setCoste(cost);
        circuito.setNeedsCredit(cost > 0);
        circuito.setActivo(true);

        List<Puerta> puertas = new ArrayList<>();

        // Añadir puerta H a cada qubit para crear superposición
        for (int i = 0; i < circuito.getQubits(); i++) {
            puertas.add(new Puerta("H", i, null));
        }

        List<String> truthTable = circuito.getTruthTable();
        int numQubits = circuito.getQubits();

        if (numQubits > 0 && truthTable != null) {
            // Simplificación: el último qubit es el objetivo.
            int targetQubit = numQubits - 1;

            for (int i = 0; i < truthTable.size(); i++) {
                // Considerar solo las filas donde la salida es 1
                if ("1".equals(truthTable.get(i))) {
                    String binaryInput = String.format("%" + (numQubits) + "s", Integer.toBinaryString(i)).replace(' ', '0');
                    
                    List<Integer> controlQubits = new ArrayList<>();
                    // Identificar los qubits de control (los que están en estado '1')
                    for (int j = 0; j < numQubits -1; j++) { // Iterar sobre los qubits de entrada
                        if (binaryInput.charAt(j) == '1') {
                            controlQubits.add(j);
                        }
                    }

                    if (controlQubits.isEmpty()) {
                        // Si no hay controles en '1' pero la salida es '1', necesitamos una puerta NOT (X)
                        // para invertir el estado inicial '0' del target.
                        puertas.add(new Puerta("X", targetQubit, null));
                    } else {
                        // Para cada qubit de control, creamos una puerta CNOT.
                        // Esto es una simplificación. Un circuito real usaría puertas Toffoli (CCNOT) para múltiples controles.
                        for (int controlQubit : controlQubits) {
                           puertas.add(new Puerta("CNOT", targetQubit, controlQubit));
                        }
                    }
                }
            }
        }

        circuito.setPuertas(puertas);
        return circuitoRepository.save(circuito);
    }

    @Transactional
    public Circuito updateCircuito(Long id, Circuito circuitoDetails) {
        Circuito circuito = getCircuitoById(id);

        circuito.setNombre(circuitoDetails.getNombre());
        circuito.setDescripcion(circuitoDetails.getDescripcion());
        circuito.setUbicacion(circuitoDetails.getUbicacion());
        circuito.setCoste(circuitoDetails.getCoste());

        return circuitoRepository.save(circuito);
    }

    @Transactional
    public void deleteCircuito(Long id) {
        Circuito circuito = getCircuitoById(id);
        circuito.setActivo(false);
        circuitoRepository.save(circuito);
    }

    public List<Circuito> getCircuitosByCosteMinimoOrMayor(double costeMinimo) {
        return circuitoRepository.findByCosteGreaterThanEqual(costeMinimo);
    }

    public boolean verificarDisponibilidadCredito(Long circuitoId, double creditoDisponible) {
        Circuito circuito = getCircuitoById(circuitoId);
        return creditoDisponible >= circuito.getCoste();
    }
}
