package com.fullstack.inventario.Service;

import com.fullstack.inventario.Repository.*;
import com.fullstack.inventario.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public ProductoService(ProductoRepository productoRepository, MovimientoRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerAlertasBajoStock() {
        // Regla: stockActual <= stockMinimo
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo())
                .collect(Collectors.toList());
    }

    @Transactional
    public Producto ajustarStock(Long id, AjusteStockDTO ajuste) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Regla: No permitir stock negativo
        int nuevoStock = producto.getStockActual() + ajuste.getCantidad();
        if (nuevoStock < 0) {
            throw new RuntimeException("Operación no permitida: El stock no puede ser negativo");
        }

        // Actualizar stock
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

        // Regla: Generar entrada inmutable de movimiento
        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProducto(producto);
        movimiento.setTipo(ajuste.getTipo()); // ENTRADA, SALIDA, AJUSTE, MERMA
        movimiento.setCantidad(ajuste.getCantidad());
        movimiento.setReferencia(ajuste.getReferencia()); // Ej: TX-00123
        movimientoRepository.save(movimiento);

        return producto;
    }

    @Transactional
    public void eliminarLogico(Long id) {
        // Regla: Si tiene movimientos, se marca como INACTIVO en lugar de borrar[cite: 1]
        Producto producto = productoRepository.findById(id).orElseThrow();
        boolean tieneMovimientos = movimientoRepository.existsByProductoId(id);

        if (tieneMovimientos) {
            // Suponiendo que añadimos un campo 'estado' o similar
            // producto.setActivo(false);
            productoRepository.save(producto);
        } else {
            productoRepository.delete(producto);
        }
    }

    // Métodos estándar: guardar, buscarTodos, buscarPorId...
}
