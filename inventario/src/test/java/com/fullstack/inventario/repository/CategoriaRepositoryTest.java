package com.fullstack.inventario.repository;

import com.fullstack.inventario.model.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe guardar y recuperar una categoría")
    void guardarYRecuperar_Exito() {
        Categoria categoria = Categoria.builder().nombre("SNACKS").build();
        
        Categoria guardada = categoriaRepository.save(categoria);
        Optional<Categoria> recuperada = categoriaRepository.findById(guardada.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getNombre()).isEqualTo("SNACKS");
    }

    @Test
    @DisplayName("Debe eliminar una categoría")
    void eliminar_Exito() {
        Categoria categoria = entityManager.persistFlushFind(Categoria.builder().nombre("DELETEME").build());
        
        categoriaRepository.deleteById(categoria.getId());
        
        Optional<Categoria> eliminada = categoriaRepository.findById(categoria.getId());
        assertThat(eliminada).isNotPresent();
    }
}
