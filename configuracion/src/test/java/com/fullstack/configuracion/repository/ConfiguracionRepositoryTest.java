package com.fullstack.configuracion.repository;

import com.fullstack.configuracion.model.ConfiguracionGlobal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
class ConfiguracionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConfiguracionRepository repository;

    @Test
    void findByClave_GivenClaveExistente_ShouldReturnConfiguracion() {
        ConfiguracionGlobal config = new ConfiguracionGlobal();
        config.setClave("TEST_CLAVE");
        config.setValor("TEST_VALOR");
        config.setFechaModificacion(LocalDateTime.now());
        config.setUsuarioId(1L); // Campo obligatorio añadido
        entityManager.persistAndFlush(config);

        Optional<ConfiguracionGlobal> found = repository.findByClave("TEST_CLAVE");

        assertThat(found).isPresent();
        assertThat(found.get().getValor()).isEqualTo("TEST_VALOR");
    }
}