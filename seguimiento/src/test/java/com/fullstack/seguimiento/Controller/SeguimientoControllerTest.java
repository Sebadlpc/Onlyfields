package com.fullstack.seguimiento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.seguimiento.Controller.SeguimientoController;
import com.fullstack.seguimiento.Service.SeguimientoService;
import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeguimientoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva Spring Security para la prueba
class SeguimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeguimientoService seguimientoService;

    @Test
    void crearFicha_ShouldReturnCreated() throws Exception {
        FichaClienteDTO requestDTO = new FichaClienteDTO(null, 1L, null, null, null, null, null);
        FichaClienteDTO responseDTO = new FichaClienteDTO(1L, 1L, null, null, null, null, null);

        when(seguimientoService.crearFicha(eq(10L), any(FichaClienteDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/seguimiento/fichas")
                        .header("X-Usuario-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerFichaCliente_ShouldReturnFicha() throws Exception {
        FichaClienteDTO responseDTO = new FichaClienteDTO(1L, 1L, null, null, null, null, null);
        when(seguimientoService.obtenerFichaPorCliente(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/seguimiento/fichas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(1L));
    }

    @Test
    void agregarMedicion_ShouldReturnCreated() throws Exception {
        MedicionCorporalDTO requestDTO = new MedicionCorporalDTO();
        MedicionCorporalDTO responseDTO = new MedicionCorporalDTO();
        responseDTO.setId(1L);

        when(seguimientoService.agregarMedicion(eq(1L), any(MedicionCorporalDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/seguimiento/fichas/1/mediciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerMediciones_ShouldReturnListOfMediciones() throws Exception {
        MedicionCorporalDTO responseDTO = new MedicionCorporalDTO();
        responseDTO.setId(1L);
        when(seguimientoService.obtenerHistorialMediciones(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/v1/seguimiento/fichas/1/mediciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}