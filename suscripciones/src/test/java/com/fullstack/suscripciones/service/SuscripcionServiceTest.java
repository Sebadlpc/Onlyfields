package com.fullstack.suscripciones.service;

import com.fullstack.suscripciones.client.NotificacionClient;
import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.dto.CongelarRequestDTO;
import com.fullstack.suscripciones.dto.SuscripcionRequestDTO;
import com.fullstack.suscripciones.dto.SuscripcionResponseDTO;
import com.fullstack.suscripciones.model.HistorialEstado;
import com.fullstack.suscripciones.model.Plan;
import com.fullstack.suscripciones.model.Suscripcion;
import com.fullstack.suscripciones.repository.HistorialEstadoRepository;
import com.fullstack.suscripciones.repository.PlanRepository;
import com.fullstack.suscripciones.repository.SuscripcionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionServiceTest {

    @Mock private SuscripcionRepository suscripcionRepository;
    @Mock private PlanRepository planRepository;
    @Mock private HistorialEstadoRepository historialRepository;
    @Mock private UsuarioClient usuarioClient;
    @Mock private NotificacionClient notificacionClient;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private Suscripcion suscripcionActiva;
    private Plan planMock;

    @BeforeEach
    void setUp() {
        planMock = Plan.builder().id(1L).nombre("MENSUAL").duracionDias(30).build();
        suscripcionActiva = Suscripcion.builder()
                .id(1L)
                .clienteId(1L)
                .plan(planMock)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(30))
                .estado("ACTIVA")
                .diasCongelados(0)
                .build();
    }

    @Test
    @DisplayName("Debe crear una suscripción exitosamente")
    void crearSuscripcion_Exito() {
        SuscripcionRequestDTO request = new SuscripcionRequestDTO(1L, 1L, LocalDate.now());
        // Simula que el cliente existe
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(new Object()); 
        when(suscripcionRepository.findByClienteIdAndEstado(1L, "ACTIVA")).thenReturn(Optional.empty());
        when(planRepository.findById(1L)).thenReturn(Optional.of(planMock));
        when(suscripcionRepository.save(any(Suscripcion.class))).thenReturn(suscripcionActiva);

        SuscripcionResponseDTO resultado = suscripcionService.crearSuscripcion(request);

        assertThat(resultado.getEstado()).isEqualTo("ACTIVA");
        verify(historialRepository).save(any(HistorialEstado.class));
        verify(usuarioClient).actualizarEstadoUsuario(anyLong(), any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente ya tiene una suscripción activa")
    void crearSuscripcion_ClienteYaActivo_LanzaExcepcion() {
        SuscripcionRequestDTO request = new SuscripcionRequestDTO(1L, 1L, LocalDate.now());
        // Simula que el cliente existe
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(new Object());
        when(suscripcionRepository.findByClienteIdAndEstado(1L, "ACTIVA")).thenReturn(Optional.of(suscripcionActiva));

        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Debe congelar una suscripción activa")
    void congelar_Exito() {
        CongelarRequestDTO request = new CongelarRequestDTO(10, "Vacaciones");
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));

        suscripcionService.congelar(1L, request);

        assertThat(suscripcionActiva.getEstado()).isEqualTo("CONGELADA");
        assertThat(suscripcionActiva.getDiasCongelados()).isEqualTo(10);
        verify(historialRepository).save(any(HistorialEstado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al congelar si excede el límite de días")
    void congelar_ExcedeLimite_LanzaExcepcion() {
        suscripcionActiva.setDiasCongelados(25);
        CongelarRequestDTO request = new CongelarRequestDTO(10, "Vacaciones");
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));

        assertThatThrownBy(() -> suscripcionService.congelar(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("límite máximo");
    }

    @Test
    @DisplayName("Debe reactivar una suscripción congelada")
    void reactivar_Exito() {
        suscripcionActiva.setEstado("CONGELADA");
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));

        suscripcionService.reactivar(1L);

        assertThat(suscripcionActiva.getEstado()).isEqualTo("ACTIVA");
        verify(historialRepository).save(any(HistorialEstado.class));
    }

    @Test
    @DisplayName("Debe cancelar una suscripción")
    void cancelar_Exito() {
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcionActiva));

        suscripcionService.cancelar(1L);

        assertThat(suscripcionActiva.getEstado()).isEqualTo("CANCELADA");
        verify(historialRepository).save(any(HistorialEstado.class));
        verify(usuarioClient).actualizarEstadoUsuario(anyLong(), any());
    }

    @Test
    @DisplayName("Debe verificar vencimientos y actualizar estado")
    void verificarVencimientos_Exito() {
        Suscripcion vencida = Suscripcion.builder()
                .id(2L).clienteId(2L).plan(planMock)
                .fechaFin(LocalDate.now().minusDays(1)).estado("ACTIVA").build();
        when(suscripcionRepository.findAllByEstado("ACTIVA")).thenReturn(List.of(suscripcionActiva, vencida));

        suscripcionService.verificarVencimientos();

        assertThat(vencida.getEstado()).isEqualTo("VENCIDA");
        verify(historialRepository).save(any(HistorialEstado.class));
        verify(notificacionClient).enviarNotificacion(any());
        verify(usuarioClient).actualizarEstadoUsuario(eq(2L), any());
    }
    
    @Test
    @DisplayName("Debe listar suscripciones por cliente")
    void listarPorCliente_Exito() {
        when(suscripcionRepository.findByClienteId(1L)).thenReturn(List.of(suscripcionActiva));
        
        List<SuscripcionResponseDTO> resultado = suscripcionService.listarPorCliente(1L);
        
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getClienteId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Debe obtener el historial de una suscripción")
    void obtenerHistorial_Exito() {
        when(historialRepository.findBySuscripcionIdOrderByIdDesc(1L)).thenReturn(List.of(new HistorialEstado()));
        
        List<HistorialEstado> resultado = suscripcionService.obtenerHistorial(1L);
        
        assertThat(resultado).hasSize(1);
    }
}