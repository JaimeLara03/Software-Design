package edu.uclm.esi.iso2.circuits.domain.circuits.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uclm.esi.iso2.circuits.domain.circuits.model.Circuito;
import java.util.List;
import java.util.Optional;

@Repository
public interface CircuitoRepository extends JpaRepository<Circuito, Long> {

    List<Circuito> findByActivoTrue();

    Optional<Circuito> findByIdAndActivoTrue(Long id);

    List<Circuito> findByCosteGreaterThanEqual(double coste);
}
