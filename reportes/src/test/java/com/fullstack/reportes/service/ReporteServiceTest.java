package com.fullstack.reportes.service;

import com.fullstack.reportes.client.InventarioClient;
import com.fullstack.reportes.client.PosClient;
import com.fullstack.reportes.client.ReservasClient;
import com.fullstack.reportes.client.SuscripcionesClient;
import com.fullstack.reportes.model.ReporteGenerado;
import com.fullstack.reportes.repository.ReporteGeneradoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteGeneradoRepository reporteRepository;
    @Mock
    private PosClient posClient;
    @Mock
    private ReservasClient reservasClient;
    @Mock
    private SuscripcionesClient suscripcionesClient;
    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private ReporteService reporteService;

    private ReporteGenerado reporteBase;

    @BeforeEach
    void setUp() {
        reporteBase = new ReporteGenerado();
        reporteBase.setTipo("FINANCIERO");
    }

    @Test
    void generarReporte_Financiero_ShouldCallPosAndReservasClients() {
        when(reporteRepository.save(any(ReporteGenerado.class))).thenAnswer(i -> i.getArgument(0));
        when(posClient.obtenerTransacciones()).thenReturn(Collections.nCopies(5, new Object()));
        when(reservasClient.obtenerReservas()).thenReturn(Collections.nCopies(10, new Object()));

        ReporteGenerado resultado = reporteService.generarReporte(reporteBase);

        assertThat(resultado.getParametros()).contains("\"transacciones\": 5", "\"reservas\": 10");
        verify(posClient).obtenerTransacciones();
        verify(reservasClient).obtenerReservas();
        verify(inventarioClient, never()).obtenerProductos();
    }

    @Test
    void generarReporte_Inventario_ShouldCallInventarioClient() {
        reporteBase.setTipo("INVENTARIO");
        when(reporteRepository.save(any(ReporteGenerado.class))).thenAnswer(i -> i.getArgument(0));
        when(inventarioClient.obtenerProductos()).thenReturn(Collections.nCopies(20, new Object()));

        ReporteGenerado resultado = reporteService.generarReporte(reporteBase);

        assertThat(resultado.getParametros()).contains("\"productos\": 20");
        verify(inventarioClient).obtenerProductos();
        verify(posClient, never()).obtenerTransacciones();
    }

    @Test
    void generarReporte_WhenFeignClientFails_ShouldSaveReportWithError() {
        when(reporteRepository.save(any(ReporteGenerado.class))).thenAnswer(i -> i.getArgument(0));
        when(posClient.obtenerTransacciones()).thenThrow(new RuntimeException("MS POS no disponible"));

        ReporteGenerado resultado = reporteService.generarReporte(reporteBase);

        assertThat(resultado.getParametros()).contains("\"error\": \"Fallo de comunicación: MS POS no disponible\"");
    }

    @Test
    void obtenerPorId_GivenExistingId_ShouldReturnReporte() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteBase));
        ReporteGenerado resultado = reporteService.obtenerPorId(1L);
        assertThat(resultado).isEqualTo(reporteBase);
    }

    @Test
    void obtenerPorId_GivenNonExistingId_ShouldThrowException() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reporteService.obtenerPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontró el reporte con ID: 99");
    }

    @Test
    void eliminarReporte_GivenExistingId_ShouldDelete() {
        when(reporteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reporteRepository).deleteById(1L);

        reporteService.eliminarReporte(1L);

        verify(reporteRepository).deleteById(1L);
    }

    @Test
    void eliminarReporte_GivenNonExistingId_ShouldThrowException() {
        when(reporteRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> reporteService.eliminarReporte(99L))
                .isInstanceOf(RuntimeException.class);
    }
}