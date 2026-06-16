package com.fullstack.accesos.service;

import com.fullstack.accesos.client.ReservasClient;
import com.fullstack.accesos.client.SuscripcionesClient;
import com.fullstack.accesos.client.UsuarioClient;
import com.fullstack.accesos.dto.external.UsuarioExternoDTO;
import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.model.TipoAcceso;
import com.fullstack.accesos.repository.QrTokenRepository;
import com.fullstack.accesos.repository.RegistroAccesoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccesosServiceTest {

    @Mock
    private QrTokenRepository qrTokenRepository;

    @Mock
    private RegistroAccesoRepository registroAccesoRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private SuscripcionesClient suscripcionesClient;

    @Mock
    private ReservasClient reservasClient;

    @InjectMocks
    private AccesosService accesosService;

    private Long clienteId;
    private String tokenString;

    @BeforeEach
    void setUp() {
        clienteId = 10L;
        tokenString = "token-secreto-123";
    }

    // ==========================================
    // TESTS PARA: generarQr()
    // ==========================================

    @Test
    @DisplayName("Debe generar un QR exitosamente si el usuario está ACTIVO")
    void generarQr_UsuarioActivo_Exito() {
        // Arrange
        UsuarioExternoDTO usuarioMock = new UsuarioExternoDTO();
        usuarioMock.setEstado("ACTIVO");

        when(usuarioClient.obtenerUsuarioPorId(clienteId)).thenReturn(usuarioMock);
        when(qrTokenRepository.save(any(QrToken.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        QrToken resultado = accesosService.generarQr(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteId, resultado.getClienteId());
        assertFalse(resultado.isUsado());
        verify(usuarioClient, times(1)).obtenerUsuarioPorId(clienteId);
        verify(qrTokenRepository, times(1)).save(any(QrToken.class));
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si el usuario NO está ACTIVO")
    void generarQr_UsuarioInactivo_LanzaExcepcion() {
        // Arrange
        UsuarioExternoDTO usuarioMock = new UsuarioExternoDTO();
        usuarioMock.setEstado("INACTIVO");

        when(usuarioClient.obtenerUsuarioPorId(clienteId)).thenReturn(usuarioMock);

        // Act & Assert (Estilo AssertJ recomendado por el profe)
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> {
                    accesosService.generarQr(clienteId);
                }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ACTIVO");

        verify(qrTokenRepository, never()).save(any());
    }

    // ==========================================
    // TESTS PARA: validarEntrada()
    // ==========================================

    @Test
    @DisplayName("Debe DENEGAR acceso si el token no existe en BD")
    void validarEntrada_TokenNoExiste_Denegado() {
        // Arrange
        when(qrTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.validarEntrada(tokenString);

        // Assert
        assertEquals(ResultadoAcceso.DENEGADO, resultado.getResultado());
        assertTrue(resultado.getMotivoRechazo().contains("inválido o inexistente"));
    }

    @Test
    @DisplayName("Debe DENEGAR acceso si el token ya está usado")
    void validarEntrada_TokenYaUsado_Denegado() {
        // Arrange
        QrToken tokenUsado = QrToken.builder().clienteId(clienteId).usado(true).build();
        when(qrTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(tokenUsado));
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.validarEntrada(tokenString);

        // Assert
        assertEquals(ResultadoAcceso.DENEGADO, resultado.getResultado());
        assertTrue(resultado.getMotivoRechazo().contains("ya fue utilizado"));
    }

    @Test
    @DisplayName("Debe DENEGAR acceso si el token expiró")
    void validarEntrada_TokenExpirado_Denegado() {
        // Arrange
        QrToken tokenExpirado = QrToken.builder()
                .clienteId(clienteId)
                .usado(false)
                .fechaExpiracion(LocalDateTime.now().minusHours(1)) // Expiró hace 1 hora
                .build();

        when(qrTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(tokenExpirado));
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.validarEntrada(tokenString);

        // Assert
        assertEquals(ResultadoAcceso.DENEGADO, resultado.getResultado());
        assertTrue(resultado.getMotivoRechazo().contains("ha expirado"));
    }

    // ==========================================
    // TESTS PARA: obtenerActivos()
    // ==========================================

    @Test
    @DisplayName("Debe calcular correctamente el aforo de clientes activos")
    void obtenerActivos_CalculoCorrecto() {
        // Arrange
        RegistroAcceso entradaCliente1 = RegistroAcceso.builder()
                .clienteId(1L).tipo(TipoAcceso.ENTRADA).resultado(ResultadoAcceso.PERMITIDO).build();

        RegistroAcceso entradaCliente2 = RegistroAcceso.builder()
                .clienteId(2L).tipo(TipoAcceso.ENTRADA).resultado(ResultadoAcceso.PERMITIDO).build();

        RegistroAcceso salidaCliente1 = RegistroAcceso.builder()
                .clienteId(1L).tipo(TipoAcceso.SALIDA).build();

        // Cliente 1 entró y salió. Cliente 2 entró y sigue adentro.
        when(registroAccesoRepository.findAll()).thenReturn(List.of(entradaCliente1, entradaCliente2, salidaCliente1));

        // Act
        List<RegistroAcceso> activos = accesosService.obtenerActivos();

        // Assert
        assertEquals(1, activos.size(), "Solo debería quedar 1 cliente activo");
        assertEquals(2L, activos.get(0).getClienteId(), "El cliente activo debería ser el ID 2");
    }
    // ==========================================
    // TESTS COMPLEMENTARIOS PARA SUBIR COBERTURA
    // ==========================================

    @Test
    @DisplayName("Debe PERMITIR entrada si el cliente tiene suscripción ACTIVA")
    void validarEntrada_SuscripcionActiva_Permitido() {
        // Arrange
        QrToken tokenValido = QrToken.builder()
                .clienteId(clienteId).usado(false).fechaExpiracion(LocalDateTime.now().plusHours(2)).build();

        SuscripcionesClient.SuscripcionDTO suscripcionMock = mock(SuscripcionesClient.SuscripcionDTO.class);
        when(suscripcionMock.estado()).thenReturn("ACTIVA");

        when(qrTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(tokenValido));
        when(suscripcionesClient.obtenerSuscripcionesPorCliente(clienteId)).thenReturn(List.of(suscripcionMock));
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.validarEntrada(tokenString);

        // Assert
        assertEquals(ResultadoAcceso.PERMITIDO, resultado.getResultado());
        assertTrue(tokenValido.isUsado());
    }

    @Test
    @DisplayName("Debe PERMITIR entrada si falla suscripción pero tiene Reserva para HOY")
    void validarEntrada_ReservaHoy_Permitido() {
        // Arrange
        QrToken tokenValido = QrToken.builder()
                .clienteId(clienteId).usado(false).fechaExpiracion(LocalDateTime.now().plusHours(2)).build();

        ReservasClient.ReservaDTO reservaMock = mock(ReservasClient.ReservaDTO.class);
        when(reservaMock.estado()).thenReturn("CONFIRMADA");
        when(reservaMock.fechaInicio()).thenReturn(LocalDateTime.now().minusMinutes(30));
        when(reservaMock.fechaFin()).thenReturn(LocalDateTime.now().plusHours(1));

        when(qrTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(tokenValido));
        when(suscripcionesClient.obtenerSuscripcionesPorCliente(clienteId)).thenThrow(new RuntimeException("Error ms-suscripciones simulado"));
        when(reservasClient.obtenerReservasPorCliente(clienteId)).thenReturn(List.of(reservaMock));
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.validarEntrada(tokenString);

        // Assert
        assertEquals(ResultadoAcceso.PERMITIDO, resultado.getResultado());
    }

    @Test
    @DisplayName("Debe DENEGAR entrada si no tiene suscripción ni reserva")
    void validarEntrada_SinBeneficios_Denegado() {
        // Arrange
        QrToken tokenValido = QrToken.builder()
                .clienteId(clienteId).usado(false).fechaExpiracion(LocalDateTime.now().plusHours(2)).build();

        when(qrTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(tokenValido));
        when(suscripcionesClient.obtenerSuscripcionesPorCliente(clienteId)).thenReturn(Collections.emptyList());
        when(reservasClient.obtenerReservasPorCliente(clienteId)).thenReturn(Collections.emptyList());
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.validarEntrada(tokenString);

        // Assert (Usa AssertJ para evitar la fragilidad del String)
        org.assertj.core.api.Assertions.assertThat(resultado.getResultado()).isEqualTo(ResultadoAcceso.DENEGADO);
        org.assertj.core.api.Assertions.assertThat(resultado.getMotivoRechazo()).isNotBlank();
    }

    @Test
    @DisplayName("Debe registrar salida exitosamente")
    void registrarSalida_Exito() {
        // Arrange
        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        RegistroAcceso resultado = accesosService.registrarSalida(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoAcceso.SALIDA, resultado.getTipo());
        assertEquals(ResultadoAcceso.PERMITIDO, resultado.getResultado());
    }

    @Test
    @DisplayName("Debe obtener el historial global y por cliente")
    void obtenerHistoriales_Exito() {
        // Arrange
        RegistroAcceso registro = new RegistroAcceso();
        when(registroAccesoRepository.findAll()).thenReturn(List.of(registro));
        when(registroAccesoRepository.findByClienteIdOrderByFechaHoraDesc(clienteId)).thenReturn(List.of(registro));

        // Act
        List<RegistroAcceso> historialGlobal = accesosService.obtenerHistorial();
        List<RegistroAcceso> historialCliente = accesosService.obtenerHistorialCliente(clienteId);

        // Assert
        assertFalse(historialGlobal.isEmpty());
        assertFalse(historialCliente.isEmpty());
    }
}