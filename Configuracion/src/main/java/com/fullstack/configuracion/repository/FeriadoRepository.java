package com.fullstack.configuracion.repository;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeriadoRepository extends JpaRepository<FeriadoBloqueo, Long> {
}