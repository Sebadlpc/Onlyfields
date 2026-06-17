package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.model.Plan;
import com.fullstack.suscripciones.model.Suscripcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SuscripcionRepositoryTest {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = entityManager.persistAndFlush(Plan.builder().nombre("TEST").duracionDias(30).build());
    }

    private Suscripcion createSuscripcion(Long clienteId, String estado, LocalDate fechaFin) {
        return Suscripcion.builder()
                .clienteId(clienteId)
                .plan(plan)
                .estado(estado)
                .fechaInicio(LocalDate.now())
                .fechaFin(fechaFin)
                .build();
    }

    @Test
    @DisplayName("Debe encontrar suscripciones por ID de cliente")
    void findByClienteId_Exito() {
        entityManager.persist(createSuscripcion(1L, "ACTIVA", LocalDate.now().plusDays(10)));
        entityManager.persist(createSuscripcion(1L, "VENCIDA", LocalDate.now().minusDays(1)));
        entityManager.persist(createSuscripcion(2L, "ACTIVA", LocalDate.now().plusDays(10)));
        entityManager.flush();

        List<Suscripcion> resultado = suscripcionRepository.findByClienteId(1L);

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("Debe encontrar una suscripción por cliente y estado")
    void findByClienteIdAndEstado_Exito() {
        entityManager.persist(createSuscripcion(3L, "ACTIVA", LocalDate.now().plusDays(10)));
        entityManager.flush();

        Optional<Suscripcion> resultado = suscripcionRepository.findByClienteIdAndEstado(3L, "ACTIVA");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isEqualTo("ACTIVA");
    }

    @Test
    @DisplayName("Debe encontrar todas las suscripciones por estado")
    void findAllByEstado_Exito() {
        entityManager.persist(createSuscripcion(4L, "ACTIVA", LocalDate.now().plusDays(10)));
        entityManager.persist(createSuscripcion(5L, "CONGELADA", LocalDate.now().plusDays(20)));
        entityManager.persist(createSuscripcion(6L, "ACTIVA", LocalDate.now().plusDays(5)));
        entityManager.flush();

        List<Suscripcion> resultado = suscripcionRepository.findAllByEstado("ACTIVA");

        assertThat(resultado).hasSize(2);
    }
}
