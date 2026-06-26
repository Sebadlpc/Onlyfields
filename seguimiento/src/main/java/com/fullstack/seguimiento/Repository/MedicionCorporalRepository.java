package com.fullstack.seguimiento.Repository;

import com.fullstack.seguimiento.model.MedicionCorporal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicionCorporalRepository extends JpaRepository<MedicionCorporal, Long> {
    List<MedicionCorporal> findByFichaClienteIdOrderByFechaMedicionDesc(Long fichaClienteId);
}
