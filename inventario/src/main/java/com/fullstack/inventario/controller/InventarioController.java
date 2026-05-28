package com.fullstack.inventario.controller;

import com.fullstack.inventario.dto.MovimientoStockDTO;
import com.fullstack.inventario.dto.ProductoRequestDTO;
import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar el inventario de productos.
 * Expone endpoints para operaciones CRUD de productos, gestión de stock,
 * y consulta de categorías y movimientos.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventarioController {

    // Inyección de dependencias del servicio de inventario.
    private final InventarioService inventarioService;

    // ------------------- Endpoints para Productos -------------------

    /**
     * Crea un nuevo producto en el inventario.
     * @param dto El DTO con la información del producto a crear.
     */
    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        Producto nuevoProducto = inventarioService.registrarProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    /**
     * Obtiene una lista de todos los productos en el inventario.
     */
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = inventarioService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene un producto específico por su ID.
     * @param id El ID del producto a buscar.
     */
    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = inventarioService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Actualiza la información de un producto existente.
     * @param id El ID del producto a actualizar.
     * @param dto El DTO con la nueva información del producto.
     */
    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        Producto productoActualizado = inventarioService.actualizarProducto(id, dto);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Elimina un producto del inventario por su ID.
     * @param id El ID del producto a eliminar.
     */
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        inventarioService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------- Endpoints para Stock y Movimientos -------------------

    /**
     * Actualiza el stock de un producto (entrada o salida).
     * @param id El ID del producto cuyo stock se va a actualizar.
     * @param dto El DTO con los detalles del movimiento (tipo y cantidad).
     */
    @PutMapping("/productos/{id}/stock")
    public ResponseEntity<String> actualizarStock(@PathVariable Long id, @Valid @RequestBody MovimientoStockDTO dto) {
        inventarioService.actualizarStock(id, dto);
        return ResponseEntity.ok("Stock actualizado exitosamente (" + dto.getTipo() + ")");
    }

    /**
     * Obtiene una lista de productos que están por debajo de su stock mínimo.
     */
    @GetMapping("/productos/alertas")
    public ResponseEntity<List<Producto>> obtenerAlertas() {
        List<Producto> alertas = inventarioService.obtenerAlertasCriticas();
        return ResponseEntity.ok(alertas);
    }

    /**
     * Obtiene el historial de movimientos de stock para un producto específico.
     * @param id El ID del producto.
     */
    @GetMapping("/productos/{id}/movimientos")
    public ResponseEntity<List<MovimientoStock>> obtenerMovimientos(@PathVariable Long id) {
        List<MovimientoStock> movimientos = inventarioService.obtenerHistorialMovimientos(id);
        return ResponseEntity.ok(movimientos);
    }

    // ------------------- Endpoints para Categorías -------------------

    /**
     * Obtiene una lista de todas las categorías de productos.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> categorias = inventarioService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }
}
