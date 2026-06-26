package com.fullstack.inventario.service;

import com.fullstack.inventario.dto.MovimientoStockDTO;
import com.fullstack.inventario.dto.ProductoRequestDTO;
import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.repository.CategoriaRepository;
import com.fullstack.inventario.repository.MovimientoStockRepository;
import com.fullstack.inventario.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private MovimientoStockRepository movimientoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto productoMock;
    private Categoria categoriaMock;
    private ProductoRequestDTO productoRequestDTO;

    @BeforeEach
    void setUp() {
        categoriaMock = Categoria.builder().id(1L).nombre("BEBIDAS").build();
        productoMock = Producto.builder()
                .id(1L)
                .nombre("Coca-Cola")
                .categoria(categoriaMock)
                .precioVenta(new BigDecimal("2.5"))
                .stockActual(100)
                .stockMinimo(20)
                .build();
        productoRequestDTO = ProductoRequestDTO.builder()
                .nombre("Coca-Cola")
                .categoriaId(1L)
                .precioVenta(new BigDecimal("2.5"))
                .stockActual(100)
                .stockMinimo(20)
                .build();
    }

    @Test
    @DisplayName("Debe registrar un producto exitosamente")
    void registrarProducto_Exito() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        Producto resultado = inventarioService.registrarProducto(productoRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Coca-Cola");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la categoría no existe al registrar")
    void registrarProducto_CategoriaNoEncontrada_LanzaExcepcion() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.registrarProducto(productoRequestDTO))
                .isInstanceOf(EntityNotFoundException.class);
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar un producto exitosamente")
    void actualizarProducto_Exito() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        Producto resultado = inventarioService.actualizarProducto(1L, productoRequestDTO);

        assertThat(resultado).isNotNull();
        verify(productoRepository).save(productoMock);
    }

    @Test
    @DisplayName("Debe actualizar el stock con una ENTRADA exitosamente")
    void actualizarStock_Entrada_Exito() {
        MovimientoStockDTO dto = new MovimientoStockDTO("ENTRADA", 50, "Compra");
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(productoMock));

        inventarioService.actualizarStock(1L, dto);

        assertThat(productoMock.getStockActual()).isEqualTo(150);
        verify(productoRepository).save(productoMock);
        verify(movimientoRepository).save(any(MovimientoStock.class));
    }

    @Test
    @DisplayName("Debe actualizar el stock con una SALIDA exitosamente")
    void actualizarStock_Salida_Exito() {
        MovimientoStockDTO dto = new MovimientoStockDTO("SALIDA", 30, "Venta");
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(productoMock));

        inventarioService.actualizarStock(1L, dto);

        assertThat(productoMock.getStockActual()).isEqualTo(70);
        verify(productoRepository).save(productoMock);
        verify(movimientoRepository).save(any(MovimientoStock.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el stock es insuficiente para una SALIDA")
    void actualizarStock_SalidaInsuficiente_LanzaExcepcion() {
        MovimientoStockDTO dto = new MovimientoStockDTO("SALIDA", 200, "Venta");
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(productoMock));

        assertThatThrownBy(() -> inventarioService.actualizarStock(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el tipo de movimiento es inválido")
    void actualizarStock_TipoInvalido_LanzaExcepcion() {
        MovimientoStockDTO dto = new MovimientoStockDTO("INVALIDO", 10, "Test");
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(productoMock));

        assertThatThrownBy(() -> inventarioService.actualizarStock(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de movimiento no válido");
    }

    @Test
    @DisplayName("Debe eliminar un producto exitosamente")
    void eliminarProducto_Exito() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        inventarioService.eliminarProducto(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar un producto que no existe")
    void eliminarProducto_NoExiste_LanzaExcepcion() {
        when(productoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> inventarioService.eliminarProducto(1L))
                .isInstanceOf(EntityNotFoundException.class);
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe listar todos los productos")
    void listarProductos_Exito() {
        when(productoRepository.findAll()).thenReturn(List.of(productoMock));
        List<Producto> resultado = inventarioService.listarProductos();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Debe obtener un producto por ID")
    void obtenerPorId_Exito() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        Producto resultado = inventarioService.obtenerPorId(1L);
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Debe obtener alertas de stock crítico")
    void obtenerAlertasCriticas_Exito() {
        when(productoRepository.findProductosBajoStockMinimo()).thenReturn(List.of(productoMock));
        List<Producto> resultado = inventarioService.obtenerAlertasCriticas();
        assertThat(resultado).isNotEmpty();
    }

    @Test
    @DisplayName("Debe obtener historial de movimientos de un producto")
    void obtenerHistorialMovimientos_Exito() {
        when(movimientoRepository.findByProductoIdOrderByFechaHoraDesc(1L)).thenReturn(Collections.emptyList());
        List<MovimientoStock> resultado = inventarioService.obtenerHistorialMovimientos(1L);
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Debe listar todas las categorías")
    void listarCategorias_Exito() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaMock));
        List<Categoria> resultado = inventarioService.listarCategorias();
        assertThat(resultado).hasSize(1);
    }
}
