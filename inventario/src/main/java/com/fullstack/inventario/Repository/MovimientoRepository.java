package com.fullstack.inventario.Repository;

import com.fullstack.inventario.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoStock, Long> {

    // Lista el historial de un producto del más reciente al más antiguo
    List<MovimientoStock> findByProductoIdOrderByFechaHoraDesc(Long productoId);

    // Verifica si existen movimientos para decidir si se hace eliminación lógica o física[cite: 1]
    boolean existsByProductoId(Long productoId);
}
