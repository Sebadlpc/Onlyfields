package com.fullstack.seguimiento.Repository;

import com.fullstack.seguimiento.model.FichaCliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.fullstack.seguimiento.model")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 🔥 Fuerza a H2 a usar su propia config limpia
class SeguimientoRepositoryTest {

    @Autowired
    private FichaClienteRepository fichaClienteRepository;

    @Test
    @DisplayName("Debe verificar de forma óptima si una ficha existe por el ID de cliente")
    void findByClienteId_RetornaFicha() {
        // Arrange
        FichaCliente ficha = FichaCliente.builder()
                .clienteId(200L)
                .antecedentesMedicos("Asma")
                .build();
        fichaClienteRepository.save(ficha);

        // Act
        Optional<FichaCliente> resultado = fichaClienteRepository.findByClienteId(200L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getAntecedentesMedicos()).isEqualTo("Asma");
    }
}