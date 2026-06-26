package com.fullstack.inventario.controller;

import com.fullstack.inventario.dto.MovimientoStockDTO;
import com.fullstack.inventario.dto.ProductoRequestDTO;
import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Inventario", description = "Operaciones relacionadas con el inventario de productos")
public class InventarioController {

    // Inyección de dependencias del servicio de inventario.
    private final InventarioService inventarioService;

    // ------------------- Endpoints para Productos -------------------

    /**
     * Crea un nuevo producto en el inventario.
     * @param dto El DTO con la información del producto a crear.
     */
    @PostMapping("/productos")
    @Operation(summary = "Crear nuevo producto", description = "Registra un nuevo producto en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<Producto> crearProducto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para crear el producto") @Valid @RequestBody ProductoRequestDTO dto) {
        Producto nuevoProducto = inventarioService.registrarProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    /**
     * Obtiene una lista de todos los productos en el inventario.
     */
    @GetMapping("/productos")
    @Operation(summary = "Listar productos", description = "Obtiene una lista de todos los productos registrados en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class))))
    })
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = inventarioService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene un producto específico por su ID.
     * @param id El ID del producto a buscar.
     */
    @GetMapping("/productos/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca y devuelve un producto específico mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Producto> obtenerProductoPorId(
            @Parameter(description = "ID del producto a buscar", required = true) @PathVariable Long id) {
        Producto producto = inventarioService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Actualiza la información de un producto existente.
     * @param id El ID del producto a actualizar.
     * @param dto El DTO con la nueva información del producto.
     */
    @PutMapping("/productos/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza la información de un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Producto> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del producto") @Valid @RequestBody ProductoRequestDTO dto) {
        Producto productoActualizado = inventarioService.actualizarProducto(id, dto);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Elimina un producto del inventario por su ID.
     * @param id El ID del producto a eliminar.
     */
    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del inventario según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Actualizar stock de producto", description = "Registra una entrada o salida de stock para un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<String> actualizarStock(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalles del movimiento de stock") @Valid @RequestBody MovimientoStockDTO dto) {
        inventarioService.actualizarStock(id, dto);
        return ResponseEntity.ok("Stock actualizado exitosamente (" + dto.getTipo() + ")");
    }

    /**
     * Obtiene una lista de productos que están por debajo de su stock mínimo.
     */
    @GetMapping("/productos/alertas")
    @Operation(summary = "Obtener alertas de stock", description = "Obtiene una lista de productos cuyo stock está por debajo del mínimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class))))
    })
    public ResponseEntity<List<Producto>> obtenerAlertas() {
        List<Producto> alertas = inventarioService.obtenerAlertasCriticas();
        return ResponseEntity.ok(alertas);
    }

    /**
     * Obtiene el historial de movimientos de stock para un producto específico.
     * @param id El ID del producto.
     */
    @GetMapping("/productos/{id}/movimientos")
    @Operation(summary = "Obtener historial de movimientos", description = "Obtiene el historial de entradas y salidas de stock para un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoStock.class)))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<List<MovimientoStock>> obtenerMovimientos(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id) {
        List<MovimientoStock> movimientos = inventarioService.obtenerHistorialMovimientos(id);
        return ResponseEntity.ok(movimientos);
    }

    // ------------------- Endpoints para Categorías -------------------

    /**
     * Obtiene una lista de todas las categorías de productos.
     */
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías", description = "Obtiene una lista de todas las categorías de productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Categoria.class))))
    })
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> categorias = inventarioService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }
}
