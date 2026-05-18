package com.fullstack.accesos.repository;

import com.fullstack.accesos.model.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {
    List<RegistroAcceso> findByClienteIdOrderByFechaHoraDesc(Long clienteId);
}
