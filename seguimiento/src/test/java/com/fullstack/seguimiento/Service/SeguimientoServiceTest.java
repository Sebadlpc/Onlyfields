package com.fullstack.seguimiento.service;

import com.fullstack.seguimiento.Service.SeguimientoService;
import com.fullstack.seguimiento.client.UsuarioClient;
import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.model.FichaCliente;
import com.fullstack.seguimiento.model.MedicionCorporal;
import com.fullstack.seguimiento.Repository.FichaClienteRepository;
import com.fullstack.seguimiento.Repository.MedicionCorporalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeguimientoServiceTest {

    @Mock
    private FichaClienteRepository fichaRepository;
    @Mock
    private MedicionCorporalRepository medicionRepository;
    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private SeguimientoService seguimientoService;

    private FichaClienteDTO fichaDTO;
    private FichaCliente ficha;

    @BeforeEach
    void setUp() {
        fichaDTO = new FichaClienteDTO(1L, 1L, "Ninguno", "Ninguna", "Ok", null, null);
        ficha = FichaCliente.builder().id(1L).clienteId(1L).build();
    }

    @Test
    void crearFicha_GivenStaffUserAndNewCliente_ShouldCreateFicha() {
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenReturn(new UsuarioClient.UsuarioResponseDTO("ACTIVO", "ROLE_STAFF"));
        when(fichaRepository.existsByClienteId(1L)).thenReturn(false);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(new UsuarioClient.UsuarioResponseDTO("ACTIVO", "ROLE_USER"));
        when(fichaRepository.save(any(FichaCliente.class))).thenReturn(ficha);

        FichaClienteDTO resultado = seguimientoService.crearFicha(10L, fichaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getClienteId()).isEqualTo(1L);
        verify(fichaRepository).save(any(FichaCliente.class));
    }

    @Test
    void crearFicha_GivenNonStaffUser_ShouldThrowException() {
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenReturn(new UsuarioClient.UsuarioResponseDTO("ACTIVO", "ROLE_USER"));
        
        assertThatThrownBy(() -> seguimientoService.crearFicha(10L, fichaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Acceso denegado");
    }

    @Test
    void crearFicha_GivenExistingFicha_ShouldThrowException() {
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenReturn(new UsuarioClient.UsuarioResponseDTO("ACTIVO", "ROLE_STAFF"));
        when(fichaRepository.existsByClienteId(1L)).thenReturn(true);

        assertThatThrownBy(() -> seguimientoService.crearFicha(10L, fichaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("FichaYaExisteException");
    }

    @Test
    void agregarMedicion_GivenValidFicha_ShouldSaveMedicion() {
        MedicionCorporalDTO medicionDTO = new MedicionCorporalDTO(null, 1L, null, 70.5, 1.75, 22.5, 35.2, 90.0, 100.0, null, "Bajar de peso");
        when(fichaRepository.findById(1L)).thenReturn(Optional.of(ficha));
        when(medicionRepository.save(any(MedicionCorporal.class))).thenAnswer(i -> i.getArgument(0));

        MedicionCorporalDTO resultado = seguimientoService.agregarMedicion(1L, medicionDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPeso()).isEqualTo(70.5);
        assertThat(resultado.getImc()).isNotNull();
    }
}