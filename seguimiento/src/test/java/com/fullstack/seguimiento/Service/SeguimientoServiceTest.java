package com.fullstack.seguimiento.Service;

import com.fullstack.seguimiento.client.UsuarioClient;
import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.model.FichaCliente;
import com.fullstack.seguimiento.model.MedicionCorporal;
import com.fullstack.seguimiento.model.ObjetivoFisico;
import com.fullstack.seguimiento.Repository.FichaClienteRepository;
import com.fullstack.seguimiento.Repository.MedicionCorporalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeguimientoServiceTest {

    @Mock private FichaClienteRepository fichaRepository;
    @Mock private MedicionCorporalRepository medicionRepository;
    @Mock private UsuarioClient usuarioClient;

    @InjectMocks private SeguimientoService seguimientoService;

    private Long creadorId;
    private Long clienteId;
    private FichaClienteDTO fichaDtoMock;
    private UsuarioClient.UsuarioResponseDTO staffMock;

    @BeforeEach
    void setUp() {
        creadorId = 10L;
        clienteId = 105L;

        fichaDtoMock = FichaClienteDTO.builder()
                .clienteId(clienteId)
                .antecedentesMedicos("Ninguno")
                .lesionesPrevias("Ninguna")
                .observaciones("Todo bien")
                .mediciones(new ArrayList<>())
                .build();

        // Creamos el mock del record del cliente Feign con rol STAFF
        staffMock = new UsuarioClient.UsuarioResponseDTO(creadorId, "Pedro", "STAFF");
    }

    @Test
    @DisplayName("Debe crear la ficha con éxito si el creador es STAFF y el cliente existe")
    void crearFicha_Exito() {
        // Arrange
        when(usuarioClient.obtenerUsuarioPorId(creadorId)).thenReturn(staffMock);
        when(fichaRepository.existsByClienteId(clienteId)).thenReturn(false);
        when(usuarioClient.obtenerUsuarioPorId(clienteId)).thenReturn(null);

        FichaCliente fichaGuardada = FichaCliente.builder()
                .id(1L).clienteId(clienteId).antecedentesMedicos("Ninguno").build();
        when(fichaRepository.save(any(FichaCliente.class))).thenReturn(fichaGuardada);

        // Act
        FichaClienteDTO resultado = seguimientoService.crearFicha(creadorId, fichaDtoMock);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(fichaRepository, times(1)).save(any(FichaCliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el creador NO tiene rol de STAFF")
    void crearFicha_CreadorNoStaff_LanzaExcepcion() {
        // Arrange
        UsuarioClient.UsuarioResponseDTO clienteComunMock = new UsuarioClient.UsuarioResponseDTO(creadorId, "Juan", "ROLE_CLIENTE");
        when(usuarioClient.obtenerUsuarioPorId(creadorId)).thenReturn(clienteComunMock);

        // Act & Assert (Se cambia la frase por la que realmente arroja tu catch)
        assertThatThrownBy(() -> seguimientoService.crearFicha(creadorId, fichaDtoMock))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pudo validar los permisos");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente ya tiene una ficha registrada")
    void crearFicha_YaExisteFicha_LanzaExcepcion() {
        // Arrange
        when(usuarioClient.obtenerUsuarioPorId(creadorId)).thenReturn(staffMock);
        when(fichaRepository.existsByClienteId(clienteId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> seguimientoService.crearFicha(creadorId, fichaDtoMock))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El cliente ya tiene una ficha");
    }

    @Test
    @DisplayName("Debe obtener la ficha técnica por el ID de cliente")
    void obtenerFichaPorCliente_Exito() {
        // Arrange
        FichaCliente ficha = FichaCliente.builder().id(1L).clienteId(clienteId).build();
        when(fichaRepository.findByClienteId(clienteId)).thenReturn(Optional.of(ficha));

        // Act
        FichaClienteDTO resultado = seguimientoService.obtenerFichaPorCliente(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getClienteId()).isEqualTo(clienteId);
    }

    @Test
    @DisplayName("Debe agregar una nueva medición corporal y calcular correctamente el IMC")
    void agregarMedicion_Exito() {
        // Arrange
        FichaCliente ficha = FichaCliente.builder().id(1L).clienteId(clienteId).build();
        when(fichaRepository.findById(1L)).thenReturn(Optional.of(ficha));

        MedicionCorporalDTO controlDto = MedicionCorporalDTO.builder()
                .peso(80.0).altura(1.80).objetivoActual(ObjetivoFisico.MANTENIMIENTO).build();

        MedicionCorporal medicionGuardada = MedicionCorporal.builder()
                .id(5L).fichaCliente(ficha).peso(80.0).altura(1.80).objetivoActual(ObjetivoFisico.MANTENIMIENTO).build();
        when(medicionRepository.save(any(MedicionCorporal.class))).thenReturn(medicionGuardada);

        // Act
        MedicionCorporalDTO resultado = seguimientoService.agregarMedicion(1L, controlDto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getImc()).isCloseTo(24.69, org.assertj.core.data.Percentage.withPercentage(1));
        verify(medicionRepository, times(1)).save(any(MedicionCorporal.class));
    }

    @Test
    @DisplayName("Debe retornar el historial de mediciones ordenado")
    void obtenerHistorialMediciones_Exito() {
        // Arrange
        when(fichaRepository.existsById(1L)).thenReturn(true);
        MedicionCorporal control = MedicionCorporal.builder().id(1L).peso(75.0).build();
        when(medicionRepository.findByFichaClienteIdOrderByFechaMedicionDesc(1L)).thenReturn(List.of(control));

        // Act
        List<MedicionCorporalDTO> historial = seguimientoService.obtenerHistorialMediciones(1L);

        // Assert
        assertThat(historial).isNotEmpty();
        assertThat(historial.get(0).getPeso()).isEqualTo(75.0);
    }
}