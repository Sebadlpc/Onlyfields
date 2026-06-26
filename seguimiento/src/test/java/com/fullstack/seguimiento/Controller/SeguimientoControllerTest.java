package com.fullstack.seguimiento.Controller;

import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.Service.SeguimientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SeguimientoController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class SeguimientoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private SeguimientoService seguimientoService;

    @Test
    @DisplayName("POST debe retornar 201 Created al registrar una ficha con el Header de Staff")
    void crearFicha_Retorna201() throws Exception {
        FichaClienteDTO entrada = FichaClienteDTO.builder().clienteId(105L).antecedentesMedicos("Ninguno").build();
        FichaClienteDTO salida = FichaClienteDTO.builder().id(1L).clienteId(105L).build();

        when(seguimientoService.crearFicha(eq(10L), any(FichaClienteDTO.class))).thenReturn(salida);

        mockMvc.perform(post("/api/v1/seguimiento/fichas")
                        .header("X-Usuario-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteId").value(105));
    }

    @Test
    @DisplayName("GET debe retornar la ficha técnica del cliente solicitado")
    void obtenerFichaCliente_Retorna200() throws Exception {
        FichaClienteDTO salida = FichaClienteDTO.builder().id(1L).clienteId(105L).build();
        when(seguimientoService.obtenerFichaPorCliente(105L)).thenReturn(salida);

        mockMvc.perform(get("/api/v1/seguimiento/fichas/cliente/105"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /mediciones debe registrar un control de peso exitosamente")
    void agregarMedicion_Retorna201() throws Exception {
        MedicionCorporalDTO control = MedicionCorporalDTO.builder().peso(82.5).altura(1.78).build();
        when(seguimientoService.agregarMedicion(eq(1L), any(MedicionCorporalDTO.class))).thenReturn(control);

        mockMvc.perform(post("/api/v1/seguimiento/fichas/1/mediciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(control)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.peso").value(82.5));
    }
}