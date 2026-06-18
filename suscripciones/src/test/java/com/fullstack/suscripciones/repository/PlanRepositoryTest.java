package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.client.NotificacionClient;
import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.model.Plan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @MockBean
    private UsuarioClient usuarioClient;

    @MockBean
    private NotificacionClient notificacionClient;

    @Test
    @DisplayName("Debe guardar y recuperar un plan")
    void guardarYRecuperar_Exito() {
        Plan plan = Plan.builder()
                .nombre("PLAN_TEST")
                .beneficios("Plan de prueba")
                .precio(new BigDecimal("99.99"))
                .duracionDias(30)
                .build();

        Plan guardado = planRepository.save(plan);
        Optional<Plan> recuperado = planRepository.findById(guardado.getId());

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getNombre()).isEqualTo("PLAN_TEST");
    }
}