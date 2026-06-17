package com.fullstack.inventario.repository;

import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder().nombre("BEBIDAS").build();
        entityManager.persistAndFlush(categoria);
    }

    private Producto createProducto(String nombre, int stockActual, int stockMinimo) {
        return Producto.builder()
                .nombre(nombre)
                .categoria(categoria)
                .precioVenta(new BigDecimal("10.0"))
                .stockActual(stockActual)
                .stockMinimo(stockMinimo)
                .build();
    }

    @Test
    @DisplayName("Debe guardar un producto y recuperarlo")
    void guardarYRecuperar_Exito() {
        Producto producto = createProducto("Coca", 100, 20);
        
        Producto guardado = productoRepository.save(producto);
        Optional<Producto> recuperado = productoRepository.findById(guardado.getId());

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getNombre()).isEqualTo("Coca");
    }

    @Test
    @DisplayName("Debe encontrar productos con bajo stock")
    void findProductosBajoStockMinimo_Exito() {
        entityManager.persist(createProducto("Normal", 100, 20));
        entityManager.persist(createProducto("Bajo Stock 1", 10, 20)); // Stock menor al mínimo
        entityManager.persist(createProducto("Bajo Stock 2", 20, 20)); // Stock igual al mínimo
        entityManager.flush();

        List<Producto> bajoStock = productoRepository.findProductosBajoStockMinimo();

        assertThat(bajoStock).hasSize(2);
        assertThat(bajoStock).extracting(Producto::getNombre)
                .containsExactlyInAnyOrder("Bajo Stock 1", "Bajo Stock 2");
    }

    @Test
    @DisplayName("findByIdForUpdate debe encontrar el producto")
    void findByIdForUpdate_Exito() {
        Producto producto = entityManager.persistAndFlush(createProducto("Test Update", 50, 10));

        Optional<Producto> recuperado = productoRepository.findByIdForUpdate(producto.getId());

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getNombre()).isEqualTo("Test Update");
    }
}
