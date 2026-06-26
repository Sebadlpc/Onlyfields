package com.fullstack.reportes.service;

import com.fullstack.reportes.client.InventarioClient;
import com.fullstack.reportes.client.PosClient;
import com.fullstack.reportes.client.ReservasClient;
import com.fullstack.reportes.model.ReporteGenerado;
import com.fullstack.reportes.repository.ReporteGeneradoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteGeneradoRepository repository;

    @Mock
    private PosClient posClient;

    @Mock
    private ReservasClient reservasClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private ReporteService reporteService;

    private ReporteGenerado reportePrueba;

    @BeforeEach
    void setUp() {
        reportePrueba = new ReporteGenerado();
        reportePrueba.setId(1L);
        reportePrueba.setUsuarioId(100L);
    }

    // ==========================================
    // TESTS DEL MÉTODO: generarReporte
    // ==========================================

    @Test
    void generarReporte_Financiero_DeberiaCalcularConDatosExternos() {
        // GIVEN (Dado un reporte financiero y clientes OK)
        reportePrueba.setTipo("FINANCIERO");
        when(posClient.obtenerTransacciones()).thenReturn(new ArrayList<>());
        when(reservasClient.obtenerReservas()).thenReturn(new ArrayList<>());
        when(repository.save(any(ReporteGenerado.class))).thenReturn(reportePrueba);

        // WHEN (Cuando se manda a generar)
        ReporteGenerado resultado = reporteService.generarReporte(reportePrueba);

        // THEN (Entonces se guardan los parámetros financieros)
        assertNotNull(resultado);
        assertTrue(resultado.getParametros().contains("transacciones"));
        verify(repository, times(1)).save(any(ReporteGenerado.class));
    }

    @Test
    void generarReporte_Inventario_DeberiaCalcularConDatosDeInventario() {
        // GIVEN (Dado un reporte de inventario)
        reportePrueba.setTipo("INVENTARIO");
        when(inventarioClient.obtenerProductos()).thenReturn(new ArrayList<>());
        when(repository.save(any(ReporteGenerado.class))).thenReturn(reportePrueba);

        // WHEN
        ReporteGenerado resultado = reporteService.generarReporte(reportePrueba);

        // THEN (Debe procesar la data de inventario)
        assertNotNull(resultado);
        assertTrue(resultado.getParametros().contains("productos"));
        verify(inventarioClient, times(1)).obtenerProductos();
        verify(repository, times(1)).save(any(ReporteGenerado.class));
    }

    @Test
    void generarReporte_TipoDesconocido_DeberiaDevolverMensajeNoImplementado() {
        // GIVEN (Dado un reporte de tipo no contemplado)
        reportePrueba.setTipo("MARKETING");
        when(repository.save(any(ReporteGenerado.class))).thenReturn(reportePrueba);

        // WHEN
        ReporteGenerado resultado = reporteService.generarReporte(reportePrueba);

        // THEN
        assertTrue(resultado.getParametros().contains("no implementado aún"));
        verify(repository, times(1)).save(any(ReporteGenerado.class));
    }

    @Test
    void generarReporte_ErrorComunicacion_DeberiaCapturarExcepcion() {
        // GIVEN (Dado un reporte financiero, pero el MS Pos está caído)
        reportePrueba.setTipo("FINANCIERO");
        when(posClient.obtenerTransacciones()).thenThrow(new RuntimeException("MS POS Caído"));
        when(repository.save(any(ReporteGenerado.class))).thenReturn(reportePrueba);

        // WHEN
        ReporteGenerado resultado = reporteService.generarReporte(reportePrueba);

        // THEN (Debe capturar el error y ponerlo en los parámetros)
        assertTrue(resultado.getParametros().contains("Fallo de comunicación"));
        verify(repository, times(1)).save(any(ReporteGenerado.class));
    }

    // ==========================================
    // TESTS DEL MÉTODO: obtenerPorId
    // ==========================================

    @Test
    void obtenerPorId_Existe_DeberiaRetornarReporte() {
        // GIVEN
        when(repository.findById(1L)).thenReturn(Optional.of(reportePrueba));

        // WHEN
        ReporteGenerado resultado = reporteService.obtenerPorId(1L);

        // THEN
        assertEquals(1L, resultado.getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_NoExiste_DeberiaLanzarExcepcion() {
        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(RuntimeException.class, () -> reporteService.obtenerPorId(99L));
    }

    // ==========================================
    // TESTS DEL MÉTODO: obtenerTodos
    // ==========================================

    @Test
    void obtenerTodos_DeberiaRetornarListaDeReportes() {
        // GIVEN
        List<ReporteGenerado> lista = new ArrayList<>();
        lista.add(reportePrueba);
        when(repository.findAll()).thenReturn(lista);

        // WHEN
        List<ReporteGenerado> resultado = reporteService.obtenerTodos();

        // THEN
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    // ==========================================
    // TESTS DEL MÉTODO: eliminarReporte
    // ==========================================

    @Test
    void eliminarReporte_Existe_DeberiaEliminarExitosamente() {
        // GIVEN
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        // WHEN
        reporteService.eliminarReporte(1L);

        // THEN
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarReporte_NoExiste_DeberiaLanzarExcepcion() {
        // GIVEN
        when(repository.existsById(99L)).thenReturn(false);

        // WHEN / THEN
        assertThrows(RuntimeException.class, () -> reporteService.eliminarReporte(99L));
        verify(repository, times(1)).existsById(99L);
        verify(repository, never()).deleteById(anyLong()); // Nunca debe llegar a borrar
    }
}