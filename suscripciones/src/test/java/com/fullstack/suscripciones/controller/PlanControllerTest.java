package com.fullstack.suscripciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.suscripciones.model.Plan;
import com.fullstack.suscripciones.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad como CSRF
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanRepository planRepository;

    private Plan planMock;

    @BeforeEach
    void setUp() {
        planMock = new Plan();
        planMock.setId(1L);
        planMock.setNombre("Plan Test");
        planMock.setPrecio(new BigDecimal("99.99"));
        planMock.setDuracionDias(30);
    }

    @Test
    void listarTodos_ShouldReturnListOfPlanes() throws Exception {
        when(planRepository.findAll()).thenReturn(Collections.singletonList(planMock));

        mockMvc.perform(get("/api/v1/planes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Plan Test"));
    }

    @Test
    void obtenerDetalle_GivenExistingId_ShouldReturnPlan() throws Exception {
        when(planRepository.findById(1L)).thenReturn(Optional.of(planMock));

        mockMvc.perform(get("/api/v1/planes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Plan Test"));
    }

    @Test
    void obtenerDetalle_GivenNonExistingId_ShouldReturnNotFound() throws Exception {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/planes/99"))
                .andExpect(status().isNotFound());
    }
}