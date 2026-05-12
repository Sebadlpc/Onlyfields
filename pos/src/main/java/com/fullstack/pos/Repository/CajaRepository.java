package com.fullstack.pos.Repository;

import com.fullstack.pos.Model.Caja;
import com.fullstack.pos.Model.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    // Busca si hay alguna caja con un estado específico (ej. ABIERTA)
    Optional<Caja> findByEstado(EstadoCaja estado);
}