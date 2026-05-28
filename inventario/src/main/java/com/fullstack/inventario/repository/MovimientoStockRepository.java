package com.fullstack.inventario.repository;

import com.fullstack.inventario.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para la entidad {@link MovimientoStock}.
 * Proporciona métodos CRUD y consultas para el historial de movimientos de stock.
 */
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    /**
     * Busca todos los movimientos de stock asociados a un producto específico,
     * ordenados por fecha y hora en orden descendente (los más recientes primero).
     *
     * @param productoId El ID del producto para el cual se busca el historial.
     * @return Una lista de movimientos de stock ordenados.
     */
    List<MovimientoStock> findByProductoIdOrderByFechaHoraDesc(Long productoId);
}
