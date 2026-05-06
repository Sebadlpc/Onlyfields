package com.fullstack.usuarios.repository;

import com.fullstack.usuarios.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    
    List<Permiso> findByModulo(String modulo);
    
    boolean existsByModuloAndAccion(String modulo, String accion);
}
