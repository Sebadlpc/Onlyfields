package com.fullstack.reportes.repository;

import com.fullstack.reportes.client.InventarioClient;
import com.fullstack.reportes.client.PosClient;
import com.fullstack.reportes.client.ReservasClient;
import com.fullstack.reportes.client.SuscripcionesClient;
import com.fullstack.reportes.model.ReporteGenerado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ReporteGeneradoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReporteGeneradoRepository repository;

    // Mockear los Feign Clients para que el contexto de @DataJpaTest cargue correctamente
    @MockBean private PosClient posClient;
    @MockBean private ReservasClient reservasClient;
    @MockBean private SuscripcionesClient suscripcionesClient;
    @MockBean private InventarioClient inventarioClient;

    @Test
    void whenSaveAndFindById_thenCorrect() {
        ReporteGenerado reporte = new ReporteGenerado();
        reporte.setTipo("FINANCIERO");
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setParametros("{\"test\": true}");

        reporte = entityManager.persistAndFlush(reporte);

        Optional<ReporteGenerado> found = repository.findById(reporte.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTipo()).isEqualTo("FINANCIERO");
    }
}