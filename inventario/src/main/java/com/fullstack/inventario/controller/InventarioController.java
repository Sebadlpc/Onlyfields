package com.fullstack.inventario.controller;

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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService service;

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearProducto(producto));
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(service.listarProductos());
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerProductoPorId(id));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(service.actualizarProducto(id, producto));
    }

    @PutMapping("/productos/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long id, @Valid @RequestBody MovimientoStock movimiento) {
        return ResponseEntity.ok(service.actualizarStock(id, movimiento));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos/alertas")
    public ResponseEntity<List<Producto>> obtenerAlertas() {
        return ResponseEntity.ok(service.obtenerAlertas());
    }

    @GetMapping("/productos/{id}/movimientos")
    public ResponseEntity<List<MovimientoStock>> obtenerMovimientos(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerMovimientosPorProducto(id));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }
}