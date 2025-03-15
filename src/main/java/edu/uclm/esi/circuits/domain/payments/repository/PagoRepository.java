package edu.uclm.esi.circuits.domain.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uclm.esi.circuits.domain.payments.model.Pago;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    List<Pago> findByUsuarioId(Long usuarioId);
    
    List<Pago> findByCircuitoId(Long circuitoId);
    
    Optional<Pago> findByReferenciaPago(String referenciaPago);
    
    List<Pago> findByEstado(String estado);
}
