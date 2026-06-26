package com.fullstack.reportes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.reportes.dto.ReporteGeneradoRequestDTO;
import com.fullstack.reportes.model.ReporteGenerado;
import com.fullstack.reportes.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteGeneradoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad para la prueba
class ReporteGeneradoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReporteService reporteService;

    @Test
    void listarTodos_ShouldReturnListOfReports() throws Exception {
        ReporteGenerado reporte = new ReporteGenerado();
        reporte.setId(1L);
        reporte.setTipo("FINANCIERO");
        when(reporteService.obtenerTodos()).thenReturn(Collections.singletonList(reporte));

        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void obtenerPorId_GivenExistingId_ShouldReturnReport() throws Exception {
        ReporteGenerado reporte = new ReporteGenerado();
        reporte.setId(1L);
        when(reporteService.obtenerPorId(1L)).thenReturn(reporte);

        mockMvc.perform(get("/api/v1/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerPorId_GivenNonExistingId_ShouldReturnNotFound() throws Exception {
        when(reporteService.obtenerPorId(99L)).thenThrow(new RuntimeException("Not Found"));

        mockMvc.perform(get("/api/v1/reportes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void generar_ShouldReturnCreated() throws Exception {
        ReporteGeneradoRequestDTO requestDTO = new ReporteGeneradoRequestDTO();
        requestDTO.setTipo("FINANCIERO");

        ReporteGenerado reporteGenerado = new ReporteGenerado();
        reporteGenerado.setId(1L);
        reporteGenerado.setTipo("FINANCIERO");

        when(reporteService.generarReporte(any(ReporteGenerado.class))).thenReturn(reporteGenerado);

        mockMvc.perform(post("/api/v1/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void eliminar_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/reportes/1"))
                .andExpect(status().isOk());
    }
}