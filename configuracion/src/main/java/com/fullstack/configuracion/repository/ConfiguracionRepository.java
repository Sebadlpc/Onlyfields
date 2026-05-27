package com.fullstack.configuracion.repository;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfiguracionRepository extends JpaRepository<ConfiguracionGlobal, Long> {
    Optional<ConfiguracionGlobal> findByClave(String clave);
}