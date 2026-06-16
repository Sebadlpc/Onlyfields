package com.fullstack.pos.Repository;

import com.fullstack.pos.model.Caja;
import com.fullstack.pos.model.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    Optional<Caja> findByEstado(EstadoCaja estado);
}
