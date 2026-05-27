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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;


    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarProducto(dto));
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(inventarioService.listarProductos());
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(inventarioService.actualizarProducto(id, dto));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        inventarioService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/productos/{id}/stock")
    public ResponseEntity<String> actualizarStock(@PathVariable Long id, @Valid @RequestBody MovimientoStockDTO dto) {
        inventarioService.actualizarStock(id, dto);
        return ResponseEntity.ok("Stock actualizado exitosamente (" + dto.getTipo() + ")");
    }

    @GetMapping("/productos/alertas")
    public ResponseEntity<List<Producto>> obtenerAlertas() {
        return ResponseEntity.ok(inventarioService.obtenerAlertasCriticas());
    }

    @GetMapping("/productos/{id}/movimientos")
    public ResponseEntity<List<MovimientoStock>> obtenerMovimientos(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerHistorialMovimientos(id));
    }


    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(inventarioService.listarCategorias());
    }
}