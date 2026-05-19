package com.fullstack.usuarios.repository;

import com.fullstack.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}