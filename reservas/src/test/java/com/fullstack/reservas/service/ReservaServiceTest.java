package com.fullstack.reservas.service;

import com.fullstack.reservas.client.AccesosClient;
import com.fullstack.reservas.client.NotificacionClient;
import com.fullstack.reservas.client.PosClient;
import com.fullstack.reservas.client.UsuarioClient;
import com.fullstack.reservas.dto.ReservaRequestDTO;
import com.fullstack.reservas.dto.ReservaResponseDTO;
import com.fullstack.reservas.exception.ConflictoReservaException;
import com.fullstack.reservas.exception.ReservaNoEncontradaException;
import com.fullstack.reservas.exception.ValidacionReservaException;
import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.models.Reserva;
import com.fullstack.reservas.repository.BloqueHorarioRepository;
import com.fullstack.reservas.repository.CanchaRepository;
import com.fullstack.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private CanchaRepository canchaRepository;
    @Mock private BloqueHorarioRepository bloqueHorarioRepository;
    @Mock private UsuarioClient usuarioClient;
    @Mock private PosClient posClient;
    @Mock private AccesosClient accesosClient;
    @Mock private NotificacionClient notificacionClient;

    @InjectMocks
    private ReservaService reservaService;

    private Cancha canchaMock;
    private Reserva reservaMock;
    private ReservaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        canchaMock = new Cancha();
        canchaMock.setId(1L);
        canchaMock.setNombre("Cancha Central");
        canchaMock.setEstado("DISPONIBLE");
        canchaMock.setTarifaHora(new BigDecimal("20.00"));

        reservaMock = new Reserva();
        reservaMock.setId(100L);
        reservaMock.setClienteId(1L);
        reservaMock.setCancha(canchaMock);
        reservaMock.setEstado("PENDIENTE_PAGO");
        reservaMock.setTotalCobrado(new BigDecimal("40.00"));
        reservaMock.setFechaInicio(LocalDateTime.now().plusDays(2));
        reservaMock.setFechaFin(LocalDateTime.now().plusDays(2).plusHours(2));

        requestDTO = new ReservaRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setCanchaId(1L);
        requestDTO.setFechaInicio(LocalDateTime.now().plusDays(2));
        requestDTO.setFechaFin(LocalDateTime.now().plusDays(2).plusHours(2));
    }

    // ==========================================
    // TESTS DE CREAR RESERVA
    // ==========================================
    @Test
    void testCrearReservaExito() {
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(java.util.Map.of("estado", "ACTIVO"));
        when(canchaRepository.findById(1L)).thenReturn(Optional.of(canchaMock));
        when(bloqueHorarioRepository.buscarChoques(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.findReservasSolapadas(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        ReservaResponseDTO response = reservaService.crearReserva(requestDTO);

        assertNotNull(response);
        assertEquals("PENDIENTE_PAGO", response.getEstado());
        verify(posClient, times(1)).generarCobro(any());
    }

    @Test
    void testCrearReservaUsuarioFalla() {
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenThrow(new RuntimeException("Error Feign"));
        assertThrows(ValidacionReservaException.class, () -> reservaService.crearReserva(requestDTO));
    }

    @Test
    void testCrearReservaDuracionInvalida() {
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(java.util.Map.of("estado", "ACTIVO"));
        requestDTO.setFechaFin(requestDTO.getFechaInicio().plusMinutes(30)); // Menos de 1 hora
        assertThrows(ValidacionReservaException.class, () -> reservaService.crearReserva(requestDTO));
    }

    @Test
    void testCrearReservaCanchaNoDisponible() {
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(java.util.Map.of("estado", "ACTIVO"));
        canchaMock.setEstado("MANTENIMIENTO");
        when(canchaRepository.findById(1L)).thenReturn(Optional.of(canchaMock));
        assertThrows(ConflictoReservaException.class, () -> reservaService.crearReserva(requestDTO));
    }

    @Test
    void testCrearReservaCanchaOcupada() {
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(java.util.Map.of("estado", "ACTIVO"));
        when(canchaRepository.findById(1L)).thenReturn(Optional.of(canchaMock));
        when(bloqueHorarioRepository.buscarChoques(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.findReservasSolapadas(any(), any(), any())).thenReturn(List.of(new Reserva())); // Simula cruce
        assertThrows(ConflictoReservaException.class, () -> reservaService.crearReserva(requestDTO));
    }

    @Test
    void testCrearReservaPosFalla() {
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(java.util.Map.of("estado", "ACTIVO"));
        when(canchaRepository.findById(1L)).thenReturn(Optional.of(canchaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);
        doThrow(new RuntimeException("POS Caído")).when(posClient).generarCobro(any()); // POS lanza error

        ReservaResponseDTO response = reservaService.crearReserva(requestDTO);
        assertNotNull(response); // No debe explotar gracias al try-catch
    }

    // ==========================================
    // TESTS DE CONFIRMAR RESERVA
    // ==========================================
    @Test
    void testConfirmarReservaExito() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        ReservaResponseDTO response = reservaService.confirmarReserva(100L);
        assertEquals("CONFIRMADA", response.getEstado());
    }

    @Test
    void testConfirmarReservaNoPendiente() {
        reservaMock.setEstado("CANCELADA");
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaMock));
        assertThrows(ValidacionReservaException.class, () -> reservaService.confirmarReserva(100L));
    }

    @Test
    void testConfirmarReservaFalloNotificacionesYAccesos() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        doThrow(new RuntimeException("Accesos Caído")).when(accesosClient).registrarQrValido(any());
        doThrow(new RuntimeException("Notificaciones Caído")).when(notificacionClient).enviarComprobante(any());

        ReservaResponseDTO response = reservaService.confirmarReserva(100L);
        assertEquals("CONFIRMADA", response.getEstado()); // La reserva sobrevive
    }

    // ==========================================
    // TESTS DE CANCELAR RESERVA
    // ==========================================
    @Test
    void testCancelarReservaMas24Horas() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any())).thenReturn(reservaMock);

        ReservaResponseDTO response = reservaService.cancelarReserva(100L);
        assertEquals("CANCELADA", response.getEstado());
        assertEquals(BigDecimal.ZERO, response.getTotalCobrado()); // 100% reembolso
    }

    @Test
    void testCancelarReservaMenos24Horas() {
        reservaMock.setFechaInicio(LocalDateTime.now().plusHours(10)); // Menos de 24h
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any())).thenReturn(reservaMock);

        ReservaResponseDTO response = reservaService.cancelarReserva(100L);
        assertEquals(new BigDecimal("12.00"), response.getTotalCobrado()); // 30% de penalidad
    }

    // ==========================================
    // TESTS DE LIMPIEZA Y LECTURA
    // ==========================================
    @Test
    void testLimpiarReservasExpiradas() {
        when(reservaRepository.buscarExpiradas(anyString(), any(LocalDateTime.class)))
                .thenReturn(List.of(reservaMock));

        reservaService.limpiarReservasExpiradas();
        verify(reservaRepository, times(1)).save(reservaMock);
        assertEquals("CANCELADA", reservaMock.getEstado());
    }

    @Test
    void testObtenerReservaPorId() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaMock));
        ReservaResponseDTO response = reservaService.obtenerReservaPorId(100L);
        assertEquals(100L, response.getId());
    }

    @Test
    void testObtenerReservaPorIdNoEncontrada() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReservaNoEncontradaException.class, () -> reservaService.obtenerReservaPorId(99L));
    }

    @Test
    void testObtenerTodasLasReservas() {
        when(reservaRepository.findAll()).thenReturn(List.of(reservaMock));
        List<ReservaResponseDTO> result = reservaService.obtenerTodasLasReservas();
        assertFalse(result.isEmpty());
    }

    @Test
    void testObtenerPorCliente() {
        when(reservaRepository.findByClienteId(1L)).thenReturn(List.of(reservaMock));
        List<ReservaResponseDTO> result = reservaService.obtenerPorCliente(1L);
        assertFalse(result.isEmpty());
    }
}