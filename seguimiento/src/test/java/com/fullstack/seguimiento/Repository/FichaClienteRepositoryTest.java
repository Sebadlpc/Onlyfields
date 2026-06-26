package com.fullstack.seguimiento.repository;

import com.fullstack.seguimiento.client.UsuarioClient;
import com.fullstack.seguimiento.model.FichaCliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

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
class FichaClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaClienteRepository repository;

    @MockBean
    private UsuarioClient usuarioClient;

    @Test
    void whenFindByClienteId_thenReturnsFicha() {
        FichaCliente ficha = new FichaCliente();
        ficha.setClienteId(1L);
        entityManager.persistAndFlush(ficha);

        Optional<FichaCliente> found = repository.findByClienteId(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getClienteId()).isEqualTo(1L);
    }

    @Test
    void whenExistsByClienteId_thenReturnsTrue() {
        FichaCliente ficha = new FichaCliente();
        ficha.setClienteId(2L);
        entityManager.persistAndFlush(ficha);

        boolean exists = repository.existsByClienteId(2L);

        assertThat(exists).isTrue();
    }
}