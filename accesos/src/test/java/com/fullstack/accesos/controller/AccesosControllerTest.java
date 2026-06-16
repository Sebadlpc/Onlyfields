package com.fullstack.accesos.controller;

import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.model.TipoAcceso;
import com.fullstack.accesos.service.AccesosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccesosController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class AccesosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Usamos MockBean para aislar el controlador y que no toque el servicio real
    @MockBean
    private AccesosService accesosService;

    @Test
    @DisplayName("Debe retornar 201 Created al generar un QR válido")
    void generarQr_Exito_Retorna201() throws Exception {
        QrToken tokenMock = QrToken.builder()
                .clienteId(1L)
                .token("hash-secreto")
                .usado(false)
                .build();

        when(accesosService.generarQr(1L)).thenReturn(tokenMock);

        mockMvc.perform(post("/api/v1/qr/generar")
                        .param("clienteId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.token").value("hash-secreto"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK al validar una entrada permitida")
    void validarQr_EntradaPermitida_Retorna200() throws Exception {
        RegistroAcceso registroPermitido = RegistroAcceso.builder()
                .clienteId(5L)
                .tipo(TipoAcceso.ENTRADA)
                .resultado(ResultadoAcceso.PERMITIDO)
                .fechaHora(LocalDateTime.now())
                .build();

        when(accesosService.validarEntrada("token-123")).thenReturn(registroPermitido);

        mockMvc.perform(post("/api/v1/accesos/validar")
                        .param("token", "token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("PERMITIDO"));
    }

    @Test
    @DisplayName("Debe retornar 403 Forbidden al intentar validar un QR denegado")
    void validarQr_EntradaDenegada_Retorna403() throws Exception {
        RegistroAcceso registroDenegado = RegistroAcceso.builder()
                .clienteId(0L)
                .tipo(TipoAcceso.ENTRADA)
                .resultado(ResultadoAcceso.DENEGADO)
                .motivoRechazo("QR inválido")
                .fechaHora(LocalDateTime.now())
                .build();

        when(accesosService.validarEntrada("token-falso")).thenReturn(registroDenegado);

        mockMvc.perform(post("/api/v1/accesos/validar")
                        .param("token", "token-falso"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultado").value("DENEGADO"))
                .andExpect(jsonPath("$.motivoRechazo").value("QR inválido"));
    }

    @Test
    @DisplayName("Debe retornar 200 OK y la lista de usuarios activos")
    void obtenerActivos_RetornaLista_Retorna200() throws Exception {
        RegistroAcceso activo = RegistroAcceso.builder()
                .clienteId(10L)
                .tipo(TipoAcceso.ENTRADA)
                .resultado(ResultadoAcceso.PERMITIDO)
                .build();

        when(accesosService.obtenerActivos()).thenReturn(List.of(activo));

        mockMvc.perform(get("/api/v1/accesos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(10));
    }
}