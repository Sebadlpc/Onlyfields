package com.fullstack.inventario.Repository;

import com.fullstack.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Para la validación de código de barras único
    Optional<Producto> findByCodigoBarras(String codigoBarras);

    // Consulta para obtener productos bajo el stock mínimo
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosBajoStockMinimo();
}