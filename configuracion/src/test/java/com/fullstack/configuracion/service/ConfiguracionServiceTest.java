package com.fullstack.configuracion.service;

import com.fullstack.configuracion.client.PosClient;
import com.fullstack.configuracion.client.ReservasClient;
import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.exception.ClaveInvalidaException;
import com.fullstack.configuracion.exception.ConfigNoEncontradaException;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.repository.ConfiguracionRepository;
import com.fullstack.configuracion.repository.FeriadoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracionServiceTest {

    @Mock
    private ConfiguracionRepository configuracionRepository;
    @Mock
    private FeriadoRepository feriadoRepository;
    @Mock
    private ReservasClient reservasClient;
    @Mock
    private PosClient posClient;

    @InjectMocks
    private ConfiguracionService configuracionService;

    private ConfiguracionGlobal configHorario;
    private ConfiguracionRequestDTO configRequestDTO;

    @BeforeEach
    void setUp() {
        configHorario = new ConfiguracionGlobal();
        configHorario.setId(1L);
        configHorario.setClave("HORARIO_CIERRE");
        configHorario.setValor("18:00");
        configHorario.setFechaModificacion(LocalDateTime.now().minusDays(1));

        configRequestDTO = new ConfiguracionRequestDTO("20:00", "Nuevo horario de cierre", 1L);
    }

    @Test
    void actualizarConfiguracion_GivenValidKey_ShouldUpdateAndPropagate() {
        when(configuracionRepository.findByClave("HORARIO_CIERRE")).thenReturn(Optional.of(configHorario));
        when(configuracionRepository.save(any(ConfiguracionGlobal.class))).thenReturn(configHorario);

        configuracionService.actualizarConfiguracion("HORARIO_CIERRE", configRequestDTO);

        ArgumentCaptor<ConfiguracionGlobal> captor = ArgumentCaptor.forClass(ConfiguracionGlobal.class);
        verify(configuracionRepository).save(captor.capture());
        assertThat(captor.getValue().getValor()).isEqualTo("20:00");

        verify(reservasClient).actualizarHorario(any(ReservasClient.HorarioDTO.class));
        verify(posClient, never()).actualizarTarifa(any());
    }

    @Test
    void actualizarConfiguracion_GivenPrecioKey_ShouldPropagateToPos() {
        ConfiguracionGlobal configPrecio = new ConfiguracionGlobal();
        configPrecio.setClave("PRECIO_BASE_CANCHA");
        configPrecio.setValor("50.00");
        ConfiguracionRequestDTO precioDTO = new ConfiguracionRequestDTO("55.00", "Nuevo precio", 1L);

        when(configuracionRepository.findByClave("PRECIO_BASE_CANCHA")).thenReturn(Optional.of(configPrecio));
        when(configuracionRepository.save(any(ConfiguracionGlobal.class))).thenReturn(configPrecio);

        configuracionService.actualizarConfiguracion("PRECIO_BASE_CANCHA", precioDTO);

        verify(posClient).actualizarTarifa(any(PosClient.TarifaDTO.class));
        verify(reservasClient, never()).actualizarHorario(any());
    }

    @Test
    void actualizarConfiguracion_GivenInvalidKey_ShouldThrowClaveInvalidaException() {
        assertThatThrownBy(() -> configuracionService.actualizarConfiguracion("CLAVE_INVALIDA", configRequestDTO))
                .isInstanceOf(ClaveInvalidaException.class);
    }

    @Test
    void obtenerPorClave_GivenExistingKey_ShouldReturnConfig() {
        when(configuracionRepository.findByClave("HORARIO_CIERRE")).thenReturn(Optional.of(configHorario));
        ConfiguracionGlobal result = configuracionService.obtenerPorClave("HORARIO_CIERRE");
        assertThat(result).isEqualTo(configHorario);
    }

    @Test
    void obtenerPorClave_GivenNonExistingKey_ShouldThrowException() {
        when(configuracionRepository.findByClave("NON_EXISTING")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> configuracionService.obtenerPorClave("NON_EXISTING"))
                .isInstanceOf(ConfigNoEncontradaException.class);
    }

    @Test
    void listarConfiguraciones_ShouldReturnList() {
        when(configuracionRepository.findAll()).thenReturn(Collections.singletonList(configHorario));
        List<ConfiguracionGlobal> result = configuracionService.listarConfiguraciones();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClave()).isEqualTo("HORARIO_CIERRE");
    }

    @Test
    void registrarFeriado_ShouldSaveFeriado() {
        FeriadoRequestDTO dto = new FeriadoRequestDTO(LocalDate.now().plusDays(1), "Test Feriado", true);
        when(feriadoRepository.save(any(FeriadoBloqueo.class))).thenAnswer(i -> i.getArgument(0));
        
        FeriadoBloqueo result = configuracionService.registrarFeriado(dto);
        
        assertThat(result.getFecha()).isEqualTo(dto.getFecha());
        assertThat(result.getMotivo()).isEqualTo(dto.getMotivo());
    }
    
    @Test
    void listarFeriados_ShouldReturnList() {
        FeriadoBloqueo feriado = new FeriadoBloqueo();
        feriado.setMotivo("Test Feriado");
        when(feriadoRepository.findAll()).thenReturn(Collections.singletonList(feriado));
        List<FeriadoBloqueo> result = configuracionService.listarFeriados();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMotivo()).isEqualTo("Test Feriado");
    }

    @Test
    void eliminarFeriado_GivenExistingId_ShouldDelete() {
        when(feriadoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(feriadoRepository).deleteById(1L);
        
        configuracionService.eliminarFeriado(1L);
        
        verify(feriadoRepository).deleteById(1L);
    }

    @Test
    void eliminarFeriado_GivenNonExistingId_ShouldThrowException() {
        when(feriadoRepository.existsById(1L)).thenReturn(false);
        
        assertThatThrownBy(() -> configuracionService.eliminarFeriado(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}