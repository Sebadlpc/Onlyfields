package com.fullstack.reservas.repository;

import com.fullstack.reservas.models.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Long> {

    // Buscar canchas por deporte (ej: "FUTBOL", "TENIS")
    List<Cancha> findByDeporte(String deporte);

    // Buscar canchas por estado (ej: "DISPONIBLE", "MANTENIMIENTO")
    List<Cancha> findByEstado(String estado);

    // Buscar canchas disponibles para un deporte específico
    List<Cancha> findByDeporteAndEstado(String deporte, String estado);

    // Verificar si ya existe una cancha con ese nombre (útil en validaciones de creación)
    boolean existsByNombre(String nombre);
}