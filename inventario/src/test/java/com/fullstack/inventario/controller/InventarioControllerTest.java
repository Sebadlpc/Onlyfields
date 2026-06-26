package com.fullstack.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.inventario.dto.MovimientoStockDTO;
import com.fullstack.inventario.dto.ProductoRequestDTO;
import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventarioController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventarioService inventarioService;

    private Producto producto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder().id(1L).nombre("Bebidas").build();
        producto = Producto.builder()
                .id(1L)
                .nombre("Coca-Cola")
                .precioVenta(new BigDecimal("1.5"))
                .stockActual(100)
                .stockMinimo(20)
                .categoria(categoria)
                .codigoBarras("123456789")
                .build();
    }

    @Test
    @DisplayName("Debe retornar 201 Created al crear un producto")
    void crearProducto_Retorna201() throws Exception {
        ProductoRequestDTO requestDTO = ProductoRequestDTO.builder()
                .nombre("Coca-Cola")
                .precioVenta(new BigDecimal("1.5"))
                .stockActual(100)
                .stockMinimo(20)
                .categoriaId(1L)
                .codigoBarras("123456789")
                .build();
        when(inventarioService.registrarProducto(any(ProductoRequestDTO.class))).thenReturn(producto);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Coca-Cola"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y la lista de productos")
    void listarProductos_Retorna200() throws Exception {
        when(inventarioService.listarProductos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Debe retornar 200 OK al obtener un producto por ID")
    void obtenerProductoPorId_Retorna200() throws Exception {
        when(inventarioService.obtenerPorId(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Coca-Cola"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK al actualizar un producto")
    void actualizarProducto_Retorna200() throws Exception {
        ProductoRequestDTO requestDTO = ProductoRequestDTO.builder()
                .nombre("Coca-Cola Zero")
                .precioVenta(new BigDecimal("1.6"))
                .stockActual(100) // Campo requerido que faltaba
                .stockMinimo(20)
                .categoriaId(1L)
                .build();
        Producto productoActualizado = Producto.builder().id(1L).nombre("Coca-Cola Zero").precioVenta(new BigDecimal("1.6")).build();
        when(inventarioService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class))).thenReturn(productoActualizado);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Coca-Cola Zero"))
                .andExpect(jsonPath("$.precioVenta").value(1.6));
    }

    @Test
    @DisplayName("Debe retornar 204 No Content al eliminar un producto")
    void eliminarProducto_Retorna204() throws Exception {
        doNothing().when(inventarioService).eliminarProducto(1L);
        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debe retornar 200 OK al actualizar el stock")
    void actualizarStock_Retorna200() throws Exception {
        MovimientoStockDTO movimientoDTO = new MovimientoStockDTO("ENTRADA", 50, "Compra a proveedor");
        doNothing().when(inventarioService).actualizarStock(eq(1L), any(MovimientoStockDTO.class));

        mockMvc.perform(put("/api/v1/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock actualizado exitosamente (ENTRADA)"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y la lista de alertas de stock")
    void obtenerAlertas_Retorna200() throws Exception {
        producto.setStockActual(15); // Forzar estado de alerta
        when(inventarioService.obtenerAlertasCriticas()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].stockActual").value(15));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y el historial de movimientos")
    void obtenerMovimientos_Retorna200() throws Exception {
        MovimientoStock movimiento = MovimientoStock.builder().id(1L).producto(producto).tipo("ENTRADA").cantidad(100).fechaHora(LocalDateTime.now()).build();
        when(inventarioService.obtenerHistorialMovimientos(1L)).thenReturn(List.of(movimiento));

        mockMvc.perform(get("/api/v1/productos/1/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("ENTRADA"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y la lista de categorías")
    void listarCategorias_Retorna200() throws Exception {
        when(inventarioService.listarCategorias()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Bebidas"));
    }
}
