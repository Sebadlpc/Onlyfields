package com.fullstack.configuracion.repository;

import com.fullstack.configuracion.model.FeriadoBloqueo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
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
class FeriadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FeriadoRepository repository;

    @Test
    void findByFecha_GivenFechaExistente_ShouldReturnFeriado() {
        FeriadoBloqueo feriado = new FeriadoBloqueo();
        feriado.setFecha(LocalDate.of(2024, 12, 25));
        feriado.setMotivo("Navidad");
        feriado.setAfectaReservas(true); // Campo obligatorio añadido
        entityManager.persistAndFlush(feriado);
        
        FeriadoBloqueo guardado = repository.save(feriado);
        Optional<FeriadoBloqueo> recuperado = repository.findById(guardado.getId());

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getMotivo()).isEqualTo("Navidad");
    }
}