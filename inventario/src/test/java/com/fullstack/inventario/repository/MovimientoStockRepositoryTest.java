package com.fullstack.inventario.repository;

import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovimientoStockRepositoryTest {

    @Autowired
    private MovimientoStockRepository movimientoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = entityManager.persist(Categoria.builder().nombre("TEST").build());
        producto = entityManager.persist(Producto.builder()
                .nombre("Test Prod")
                .categoria(categoria)
                .precioVenta(new BigDecimal("9.99"))
                .stockActual(100)
                .stockMinimo(10)
                .build());
        entityManager.flush();
    }

    private MovimientoStock createMovimiento(String tipo, int cantidad, LocalDateTime fecha) {
        return MovimientoStock.builder()
                .producto(producto)
                .tipo(tipo)
                .cantidad(cantidad)
                .fechaHora(fecha)
                .referencia("Test") // Campo requerido que faltaba
                .build();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un movimiento de stock")
    void guardarYRecuperar_Exito() {
        MovimientoStock movimiento = createMovimiento("ENTRADA", 50, LocalDateTime.now());
        
        MovimientoStock guardado = movimientoRepository.save(movimiento);
        
        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isPositive();
        assertThat(guardado.getTipo()).isEqualTo("ENTRADA");
    }

    @Test
    @DisplayName("Debe encontrar movimientos por ID de producto y ordenarlos por fecha descendente")
    void findByProductoIdOrderByFechaHoraDesc_Exito() {
        // Arrange
        entityManager.persist(createMovimiento("ENTRADA", 10, LocalDateTime.now().minusDays(1))); // Antiguo
        entityManager.persist(createMovimiento("SALIDA", 5, LocalDateTime.now())); // Reciente
        entityManager.persist(createMovimiento("ENTRADA", 20, LocalDateTime.now().minusHours(1))); // Medio
        entityManager.flush();

        // Act
        List<MovimientoStock> movimientos = movimientoRepository.findByProductoIdOrderByFechaHoraDesc(producto.getId());

        // Assert
        assertThat(movimientos).hasSize(3);
        assertThat(movimientos).isSortedAccordingTo((m1, m2) -> m2.getFechaHora().compareTo(m1.getFechaHora()));
        assertThat(movimientos.get(0).getTipo()).isEqualTo("SALIDA");
        assertThat(movimientos.get(2).getCantidad()).isEqualTo(10);
    }
}
