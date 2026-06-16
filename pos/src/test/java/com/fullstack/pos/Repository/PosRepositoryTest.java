package com.fullstack.pos.Repository;

import com.fullstack.pos.model.Caja;
import com.fullstack.pos.model.EstadoCaja;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.fullstack.pos.model")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 🔥 Fuerza a usar tu application.properties de test
class PosRepositoryTest {

    @Autowired
    private CajaRepository cajaRepository;

    @Test
    @DisplayName("Debe encontrar una caja por su estado de apertura")
    void findByEstado_RetornaCaja() {
        // Arrange
        Caja caja = Caja.builder()
                .usuarioId(1L)
                .montoInicial(BigDecimal.valueOf(10000))
                .estado(EstadoCaja.ABIERTA)
                .build();
        cajaRepository.save(caja);

        // Act
        Optional<Caja> resultado = cajaRepository.findByEstado(EstadoCaja.ABIERTA);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuarioId()).isEqualTo(1L);
    }
}