package com.fullstack.configuracion.repository;

import com.fullstack.configuracion.model.ConfiguracionGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link ConfiguracionGlobal}.
 * Proporciona métodos CRUD y consultas para las configuraciones del sistema.
 */
public interface ConfiguracionRepository extends JpaRepository<ConfiguracionGlobal, Long> {

    /**
     * Busca una configuración global por su clave única.
     * La búsqueda es insensible a mayúsculas/minúsculas si la base de datos está configurada así,
     * pero es buena práctica normalizar la clave (ej. a mayúsculas) en la capa de servicio antes de llamar a este método.
     *
     * @param clave La clave única de la configuración (ej. "TASA_IVA").
     */
    Optional<ConfiguracionGlobal> findByClave(String clave);
}
