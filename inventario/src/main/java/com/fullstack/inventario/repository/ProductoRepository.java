package com.fullstack.inventario.repository;

import com.fullstack.inventario.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Producto}.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca todos los productos cuyo stock actual es menor o igual a su stock mínimo definido.
     * Útil para generar alertas de inventario bajo.
     *
     * @return Una lista de productos que necesitan reposición.
     */
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosBajoStockMinimo();

    /**
     * Busca un producto por su ID y aplica un bloqueo pesimista de escritura (PESSIMISTIC_WRITE).
     * Esto bloquea la fila en la base de datos hasta que la transacción actual se complete,
     * evitando que otras transacciones modifiquen el mismo producto simultáneamente.
     * Es crucial para operaciones críticas como la actualización de stock.
     *
     * @param id El ID del producto a buscar y bloquear.
     * @return Un {@link Optional} que contiene el producto si se encuentra.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);
}
