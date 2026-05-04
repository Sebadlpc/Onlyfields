package com.fullstack.inventario.Controller;

import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final MovimientoService movimientoService;

    public ProductoController(ProductoService productoService, MovimientoService movimientoService) {
        this.productoService = productoService;
        this.movimientoService = movimientoService;
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        // Aplica validaciones: @NotBlank, @Positive, etc.
        return new ResponseEntity<>(productoService.guardar(producto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizar(id, producto));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Producto> ajustarStock(@PathVariable Long id, @RequestBody AjusteStockDTO ajuste) {
        // Implementa lógica de negocio: no stock negativo y alertas
        return ResponseEntity.ok(productoService.ajustarStock(id, ajuste));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id) {
        // Aplicar eliminación lógica si tiene movimientos
        productoService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<Producto>> listarAlertasStock() {
        // Filtra productos donde stockActual <= stockMinimo
        return ResponseEntity.ok(productoService.obtenerAlertasBajoStock());
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoStock>> consultarHistorial(@PathVariable Long id) {
        // Retorna historial inmutable de movimientos
        return ResponseEntity.ok(movimientoService.listarPorProducto(id));
    }
}
