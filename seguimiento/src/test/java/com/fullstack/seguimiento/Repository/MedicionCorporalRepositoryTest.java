package com.fullstack.seguimiento.repository;

import com.fullstack.seguimiento.client.UsuarioClient;
import com.fullstack.seguimiento.model.FichaCliente;
import com.fullstack.seguimiento.model.MedicionCorporal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

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
class MedicionCorporalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MedicionCorporalRepository repository;

    @MockBean
    private UsuarioClient usuarioClient;

    @Test
    void whenFindByFichaClienteId_thenReturnsMediciones() {
        FichaCliente ficha = new FichaCliente();
        ficha.setClienteId(1L);
        entityManager.persist(ficha);

        MedicionCorporal medicion1 = new MedicionCorporal();
        medicion1.setFichaCliente(ficha);
        entityManager.persist(medicion1);

        MedicionCorporal medicion2 = new MedicionCorporal();
        medicion2.setFichaCliente(ficha);
        entityManager.persist(medicion2);

        entityManager.flush();

        List<MedicionCorporal> found = repository.findByFichaClienteIdOrderByFechaMedicionDesc(ficha.getId());

        assertThat(found).hasSize(2);
    }
}