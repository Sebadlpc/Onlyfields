package com.fullstack.notificaciones.service;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.model.Notificacion;
import com.fullstack.notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository;

    @InjectMocks
    private NotificacionService service;

    private Notificacion notificacionMock;
    private NotificacionDTO dtoMock;

    @BeforeEach
    void setUp() {
        notificacionMock = Notificacion.builder()
                .id(1L)
                .destinatarioId(10L)
                .destinatarioEmail("test@test.com")
                .tipo("COMPROBANTE")
                .canal("EMAIL")
                .asunto("Confirmación")
                .cuerpo("Mensaje de prueba")
                .estado("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .intentos(0)
                .idempotencyKey("key-123")
                .build();

        dtoMock = NotificacionDTO.builder()
                .id(1L)
                .destinatarioId(10L)
                .destinatarioEmail("test@test.com")
                .tipo("COMPROBANTE")
                .canal("EMAIL")
                .asunto("Confirmación")
                .cuerpo("Mensaje de prueba")
                .idempotencyKey("key-123")
                .build();
    }

    @Test
    void testCrearNotificacionIdempotencyKeyExistente() {
        when(repository.findByIdempotencyKey("key-123")).thenReturn(Optional.of(notificacionMock));
        NotificacionDTO result = service.crearNotificacion(dtoMock);
        assertEquals(1L, result.getId());
        verify(repository, never()).save(any());
    }

    @Test
    void testCrearNotificacionNueva() {
        when(repository.findByIdempotencyKey("key-123")).thenReturn(Optional.empty());
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionMock);
        NotificacionDTO result = service.crearNotificacion(dtoMock);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void testCrearNotificacionSinIdempotencyKey() {
        dtoMock.setIdempotencyKey(null);
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionMock);
        NotificacionDTO result = service.crearNotificacion(dtoMock);
        assertEquals(1L, result.getId());
        verify(repository, never()).findByIdempotencyKey(anyString());
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void testProcesarEnvioExito() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacionMock));
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionMock);
        service.procesarEnvio(1L);
        assertEquals("ENVIADO", notificacionMock.getEstado());
        assertEquals(1, notificacionMock.getIntentos());
        verify(repository, times(1)).save(notificacionMock);
    }

    @Test
    void testProcesarEnvioNoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        service.procesarEnvio(1L);
        verify(repository, never()).save(any());
    }

    @Test
    void testProcesarEnvioInterruptedException() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacionMock));
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionMock);
        Thread.currentThread().interrupt();
        service.procesarEnvio(1L);
        assertEquals("FALLIDO", notificacionMock.getEstado());
        verify(repository, times(1)).save(notificacionMock);
    }

    @Test
    void testObtenerNotificacionEncontrada() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacionMock));
        NotificacionDTO result = service.obtenerNotificacion(1L);
        assertNotNull(result);
    }

    @Test
    void testObtenerNotificacionNoEncontrada() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        NotificacionDTO result = service.obtenerNotificacion(1L);
        assertNull(result);
    }

    @Test
    void testObtenerPorDestinatario() {
        when(repository.findByDestinatarioId(10L)).thenReturn(List.of(notificacionMock));
        List<NotificacionDTO> result = service.obtenerPorDestinatario(10L);
        assertFalse(result.isEmpty());
    }

    @Test
    void testObtenerPendientes() {
        when(repository.findByEstado("PENDIENTE")).thenReturn(List.of(notificacionMock));
        List<NotificacionDTO> result = service.obtenerPendientes();
        assertFalse(result.isEmpty());
    }

    @Test
    void testReenviarEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacionMock));
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionMock);
        service.reenviar(1L);
        assertEquals("ENVIADO", notificacionMock.getEstado());
        verify(repository, atLeastOnce()).save(any(Notificacion.class));
    }

    @Test
    void testReenviarNoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        service.reenviar(1L);
        verify(repository, never()).save(any());
    }
    @Test
    void testObtenerTodas() {
        when(repository.findAll()).thenReturn(List.of(notificacionMock));
        List<NotificacionDTO> result = service.obtenerTodas();
        assertFalse(result.isEmpty());
    }
}