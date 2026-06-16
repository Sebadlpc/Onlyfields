package com.fullstack.pos.Controller;

import com.fullstack.pos.dto.CajaDTO;
import com.fullstack.pos.dto.TransaccionDTO;
import com.fullstack.pos.model.EstadoCaja;
import com.fullstack.pos.Service.PosService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.fullstack.pos.Controller.PosController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class PosControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PosService posService;

    @Test
    @DisplayName("POST /caja/abrir debe abrir una caja y retornar 200 OK")
    void abrirCaja_Retorna200() throws Exception {
        CajaDTO cajaMock = CajaDTO.builder()
                .id(1L).usuarioId(5L).montoInicial(BigDecimal.valueOf(50000)).estado(EstadoCaja.ABIERTA).build();

        when(posService.abrirCaja(eq(5L), eq(50000.0))).thenReturn(cajaMock);

        mockMvc.perform(post("/api/v1/pos/caja/abrir")
                        .param("usuarioId", "5")
                        .param("montoInicial", "50000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ABIERTA"));
    }

    @Test
    @DisplayName("GET /caja/actual debe obtener los datos de la caja vigente")
    void obtenerCajaActual_Retorna200() throws Exception {
        CajaDTO cajaMock = CajaDTO.builder().id(1L).estado(EstadoCaja.ABIERTA).build();
        when(posService.obtenerCajaActual()).thenReturn(cajaMock);

        mockMvc.perform(get("/api/v1/pos/caja/actual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /transacciones debe retornar 201 Created al guardar una transacción")
    void registrarTransaccion_Retorna201() throws Exception {
        TransaccionDTO entrada = TransaccionDTO.builder().total(BigDecimal.valueOf(3000)).items(Collections.emptyList()).build();
        TransaccionDTO salida = TransaccionDTO.builder().id(10L).total(BigDecimal.valueOf(3000)).build();

        when(posService.registrarTransaccion(any(TransaccionDTO.class))).thenReturn(salida);

        mockMvc.perform(post("/api/v1/pos/transacciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.total").value(3000));
    }
}