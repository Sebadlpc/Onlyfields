package com.fullstack.accesos.repository;

import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.model.TipoAcceso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccesosRepositoryTest {

    @Autowired
    private QrTokenRepository qrTokenRepository;

    @Autowired
    private RegistroAccesoRepository registroAccesoRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {

        qrTokenRepository.deleteAll();
        registroAccesoRepository.deleteAll();
    }

    // ==========================================
    // TESTS PARA: QrTokenRepository
    // ==========================================

    @Test
    @DisplayName("Debe encontrar un QR buscando por su token Hash")
    void findByToken_Existe_RetornaQr() {
        // Arrange: Guardamos un token real en H2
        QrToken nuevoQr = QrToken.builder()
                .clienteId(1L)
                .token("hash-unico-123")
                .fechaExpiracion(LocalDateTime.now().plusDays(1))
                .usado(false)
                .build();
        qrTokenRepository.save(nuevoQr);

        // Act: Lo buscamos con tu método personalizado
        Optional<QrToken> resultado = qrTokenRepository.findByToken("hash-unico-123");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getClienteId());
    }

    @Test
    @DisplayName("Debe retornar Optional vacío si el token no existe")
    void findByToken_NoExiste_RetornaVacio() {
        Optional<QrToken> resultado = qrTokenRepository.findByToken("token-inventado");
        assertTrue(resultado.isEmpty());
    }

    // ==========================================
    // TESTS PARA: RegistroAccesoRepository
    // ==========================================

    @Test
    @DisplayName("Debe traer el historial del cliente ordenado por fecha descendente")
    void findByClienteIdOrderByFechaHoraDesc_RetornaListaOrdenada() {
        // Arrange: Creamos dos registros para el mismo cliente (ID 5)
        RegistroAcceso registroViejo = RegistroAcceso.builder()
                .clienteId(5L)
                .tipo(TipoAcceso.ENTRADA)
                .resultado(ResultadoAcceso.PERMITIDO)
                .build();
        // Simulamos que entró hace 2 horas
        registroViejo.setFechaHora(LocalDateTime.now().minusHours(2));

        RegistroAcceso registroNuevo = RegistroAcceso.builder()
                .clienteId(5L)
                .tipo(TipoAcceso.SALIDA)
                .resultado(ResultadoAcceso.PERMITIDO)
                .build();
        // Simulamos que salió hace 5 minutos
        registroNuevo.setFechaHora(LocalDateTime.now().minusMinutes(5));

        registroAccesoRepository.save(registroViejo);
        registroAccesoRepository.save(registroNuevo);

        // Act: Ejecutamos tu query derivada
        List<RegistroAcceso> historial = registroAccesoRepository.findByClienteIdOrderByFechaHoraDesc(5L);

        // Assert
        assertEquals(2, historial.size(), "Debería encontrar 2 registros para el cliente 5");

        // El primer elemento de la lista debe ser el más nuevo (SALIDA)
        assertEquals(TipoAcceso.SALIDA, historial.get(0).getTipo());

        // El segundo elemento debe ser el más viejo (ENTRADA)
        assertEquals(TipoAcceso.ENTRADA, historial.get(1).getTipo());
    }
}