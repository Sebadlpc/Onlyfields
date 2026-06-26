package com.fullstack.suscripciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.suscripciones.dto.CongelarRequestDTO;
import com.fullstack.suscripciones.dto.SuscripcionRequestDTO;
import com.fullstack.suscripciones.dto.SuscripcionResponseDTO;
import com.fullstack.suscripciones.model.HistorialEstado;
import com.fullstack.suscripciones.service.SuscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SuscripcionController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class SuscripcionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuscripcionService suscripcionService;

    private SuscripcionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = SuscripcionResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .planId(1L)
                .planNombre("MENSUAL")
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(30))
                .estado("ACTIVA")
                .build();
    }

    @Test
    @DisplayName("Debe retornar 201 Created al crear una suscripción")
    void crear_Retorna201() throws Exception {
        SuscripcionRequestDTO requestDTO = new SuscripcionRequestDTO(1L, 1L, LocalDate.now());
        when(suscripcionService.crearSuscripcion(any(SuscripcionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/suscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y la lista de suscripciones por cliente")
    void listarPorCliente_Retorna200() throws Exception {
        when(suscripcionService.listarPorCliente(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/suscripciones/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].clienteId").value(1));
    }

    @Test
    @DisplayName("Debe retornar 200 OK al congelar una suscripción")
    void congelar_Retorna200() throws Exception {
        CongelarRequestDTO requestDTO = new CongelarRequestDTO(10, "Vacaciones");
        responseDTO.setEstado("CONGELADA");
        when(suscripcionService.congelar(eq(1L), any(CongelarRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/suscripciones/1/congelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CONGELADA"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK al reactivar una suscripción")
    void reactivar_Retorna200() throws Exception {
        when(suscripcionService.reactivar(1L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/suscripciones/1/reactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK al cancelar una suscripción")
    void cancelar_Retorna200() throws Exception {
        responseDTO.setEstado("CANCELADA");
        when(suscripcionService.cancelar(1L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/suscripciones/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y el historial de estados")
    void obtenerHistorial_Retorna200() throws Exception {
        HistorialEstado historial = HistorialEstado.builder().id(1L).motivo("Test").build();
        when(suscripcionService.obtenerHistorial(1L)).thenReturn(List.of(historial));

        mockMvc.perform(get("/api/v1/suscripciones/1/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
