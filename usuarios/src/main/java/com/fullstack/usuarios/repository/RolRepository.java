package com.fullstack.usuarios.repository;

import com.fullstack.usuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Rol}.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre único.
     *
     * @param nombre El nombre del rol a buscar (ej. "ADMIN", "USER").
     */
    Optional<Rol> findByNombre(String nombre);
}
