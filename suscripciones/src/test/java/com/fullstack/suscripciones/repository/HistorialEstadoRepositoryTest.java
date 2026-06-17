package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.model.HistorialEstado;
import com.fullstack.suscripciones.model.Plan;
import com.fullstack.suscripciones.model.Suscripcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HistorialEstadoRepositoryTest {

    @Autowired
    private HistorialEstadoRepository historialRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Suscripcion suscripcion;

    @BeforeEach
    void setUp() {
        Plan plan = entityManager.persist(Plan.builder().nombre("TEST").duracionDias(30).build());
        suscripcion = entityManager.persist(Suscripcion.builder()
                .clienteId(1L)
                .plan(plan)
                .estado("ACTIVA")
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(30))
                .build());
        entityManager.flush();
    }

    private HistorialEstado createHistorial(String estadoNuevo, LocalDateTime fecha) {
        return HistorialEstado.builder()
                .suscripcion(suscripcion)
                .estadoNuevo(estadoNuevo)
                .fechaCambio(fecha)
                .motivo("Test")
                .build();
    }

    @Test
    @DisplayName("Debe encontrar el historial por ID de suscripción y ordenarlo por ID descendente")
    void findBySuscripcionIdOrderByIdDesc_Exito() {
        HistorialEstado h1 = entityManager.persist(createHistorial("ACTIVA", LocalDateTime.now().minusDays(1)));
        HistorialEstado h2 = entityManager.persist(createHistorial("CONGELADA", LocalDateTime.now()));
        entityManager.flush();

        List<HistorialEstado> resultado = historialRepository.findBySuscripcionIdOrderByIdDesc(suscripcion.getId());

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(h2.getId());
        assertThat(resultado.get(1).getId()).isEqualTo(h1.getId());
    }
}
