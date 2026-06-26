package com.fullstack.configuracion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.service.ConfiguracionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfiguracionController.class)
@AutoConfigureMockMvc(addFilters = false) // Correcto: Desactiva CSRF y otros filtros
class ConfiguracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConfiguracionService configuracionService;

    @Test
    void listarConfiguraciones_ShouldReturnListOfConfigs() throws Exception {
        ConfiguracionGlobal config = new ConfiguracionGlobal();
        config.setClave("TEST_CLAVE");
        when(configuracionService.listarConfiguraciones()).thenReturn(Collections.singletonList(config));

        mockMvc.perform(get("/api/v1/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clave").value("TEST_CLAVE"));
    }

    @Test
    void actualizarConfiguracion_ShouldReturnOk() throws Exception {
        ConfiguracionRequestDTO requestDTO = new ConfiguracionRequestDTO("20:00", "Nuevo horario", 1L);
        ConfiguracionGlobal configMock = new ConfiguracionGlobal();
        configMock.setValor("20:00");

        when(configuracionService.actualizarConfiguracion(eq("HORARIO_CIERRE"), any(ConfiguracionRequestDTO.class)))
                .thenReturn(configMock);

        // La solución final: enviar la cabecera que el método del controlador está esperando
        mockMvc.perform(put("/api/v1/config/HORARIO_CIERRE")
                        .header("X-User-Role", "ADMIN") // <-- La pieza clave que faltaba
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("20:00"));
    }

    @Test
    void registrarFeriado_ShouldReturnCreated() throws Exception {
        FeriadoRequestDTO requestDTO = new FeriadoRequestDTO(LocalDate.now().plusDays(1), "Test Feriado", true);
        FeriadoBloqueo feriadoMock = new FeriadoBloqueo();
        feriadoMock.setId(1L);

        when(configuracionService.registrarFeriado(any(FeriadoRequestDTO.class))).thenReturn(feriadoMock);

        mockMvc.perform(post("/api/v1/feriados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void eliminarFeriado_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/feriados/1"))
                .andExpect(status().isNoContent());
    }
}