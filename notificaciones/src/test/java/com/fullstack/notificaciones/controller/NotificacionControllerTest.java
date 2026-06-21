package com.fullstack.notificaciones.controller;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private NotificacionService service;

    @InjectMocks
    private NotificacionController controller;

    @Test
    void testEnviarComprobanteConClienteId() {
        Map<String, Object> datosReserva = new HashMap<>();
        datosReserva.put("clienteId", 10L);
        datosReserva.put("totalCobrado", 15000);

        when(service.crearNotificacion(any(NotificacionDTO.class))).thenReturn(new NotificacionDTO());

        ResponseEntity<String> response = controller.enviarComprobante(datosReserva);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).crearNotificacion(any(NotificacionDTO.class));
    }

    @Test
    void testEnviarComprobanteSinClienteId() {
        Map<String, Object> datosReserva = new HashMap<>();
        datosReserva.put("totalCobrado", 15000);

        when(service.crearNotificacion(any(NotificacionDTO.class))).thenReturn(new NotificacionDTO());

        ResponseEntity<String> response = controller.enviarComprobante(datosReserva);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).crearNotificacion(any(NotificacionDTO.class));
    }
}