package com.fullstack.usuarios.repository;

import com.fullstack.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    
    boolean existsByCorreoElectronico(String correoElectronico);
}
