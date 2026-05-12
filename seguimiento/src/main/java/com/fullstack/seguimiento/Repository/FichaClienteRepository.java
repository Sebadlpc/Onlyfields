package com.fullstack.seguimiento.Repository;

import com.fullstack.seguimiento.Model.FichaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FichaClienteRepository extends JpaRepository<FichaCliente, Long> {
    Optional<FichaCliente> findByClienteId(Long clienteId);
    boolean existsByClienteId(Long clienteId);
}
