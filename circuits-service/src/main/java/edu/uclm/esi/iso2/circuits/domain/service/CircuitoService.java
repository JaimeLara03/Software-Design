package edu.uclm.esi.iso2.circuits.domain.circuits.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.iso2.circuits.domain.circuits.model.Circuito;
import edu.uclm.esi.iso2.circuits.domain.circuits.repository.CircuitoRepository;
import edu.uclm.esi.iso2.circuits.exception.ResourceNotFoundException;

import java.util.List;

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
        circuito.setActivo(true);
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
