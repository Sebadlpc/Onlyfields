package com.fullstack.seguimiento.Repository;

import com.fullstack.seguimiento.Model.MedicionCorporal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicionCorporalRepository extends JpaRepository<MedicionCorporal, Long> {
    // Trae el historial ordenado por fecha de más reciente a más antiguo
    List<MedicionCorporal> findByFichaIdOrderByFechaDesc(Long fichaId);
}
