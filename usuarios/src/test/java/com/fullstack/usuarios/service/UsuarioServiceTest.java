package com.fullstack.usuarios.service;

import com.fullstack.usuarios.dto.AuthLoginDTO;
import com.fullstack.usuarios.dto.UsuarioRegistroDTO;
import com.fullstack.usuarios.dto.UsuarioRespuestaDTO;
import com.fullstack.usuarios.exception.CredencialesInvalidasException;
import com.fullstack.usuarios.exception.EmailYaRegistradoException;
import com.fullstack.usuarios.exception.RolNoEncontradoException;
import com.fullstack.usuarios.exception.UsuarioNoEncontradoException;
import com.fullstack.usuarios.model.Rol;
import com.fullstack.usuarios.model.Usuario;
import com.fullstack.usuarios.repository.RolRepository;
import com.fullstack.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRegistroDTO usuarioRegistroDTO;
    private Rol rol;
    private Usuario usuario;
    private AuthLoginDTO authLoginDTO;

    @BeforeEach
    void setUp() {
        usuarioRegistroDTO = UsuarioRegistroDTO.builder()
                .nombre("Test User")
                .correoElectronico("test@example.com")
                .password("password123")
                .rolId(1L)
                .build();

        rol = Rol.builder()
                .id(1L)
                .nombre("ROLE_USER")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .estado("ACTIVO")
                .fechaCreacion(LocalDateTime.now())
                .build();
        usuario.agregarRol(rol);

        authLoginDTO = AuthLoginDTO.builder()
                .correoElectronico("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    void registrarUsuario_Exito() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(anyLong())).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioRespuestaDTO respuesta = usuarioService.registrarUsuario(usuarioRegistroDTO);

        // Assert
        assertNotNull(respuesta);
        assertEquals(usuario.getId(), respuesta.getId());
        assertEquals(usuario.getNombre(), respuesta.getNombre());
        assertEquals(usuario.getEmail(), respuesta.getCorreoElectronico());
        assertEquals("ACTIVO", respuesta.getEstado());
        assertEquals("ROLE_USER", respuesta.getRolNombre());

        verify(usuarioRepository).existsByEmail(anyString());
        verify(rolRepository).findById(anyLong());
        verify(passwordEncoder).encode(anyString());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_EmailYaRegistrado() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailYaRegistradoException.class, () -> usuarioService.registrarUsuario(usuarioRegistroDTO));
        verify(usuarioRepository).existsByEmail(anyString());
        verify(rolRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_RolNoEncontrado() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RolNoEncontradoException.class, () -> usuarioService.registrarUsuario(usuarioRegistroDTO));
        verify(usuarioRepository).existsByEmail(anyString());
        verify(rolRepository).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_Exito() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        UsuarioRespuestaDTO respuesta = usuarioService.login(authLoginDTO);

        // Assert
        assertNotNull(respuesta);
        assertEquals(usuario.getId(), respuesta.getId());
        assertEquals(usuario.getEmail(), respuesta.getCorreoElectronico());

        verify(usuarioRepository).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void login_UsuarioNoEncontrado() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.login(authLoginDTO));
        verify(usuarioRepository).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_ContrasenaIncorrecta() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.login(authLoginDTO));
        verify(usuarioRepository).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void login_CuentaInactiva() {
        // Arrange
        usuario.setEstado("INACTIVO");
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.login(authLoginDTO));
        verify(usuarioRepository).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void listarTodos_Exito() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        // Act
        List<UsuarioRespuestaDTO> respuesta = usuarioService.listarTodos();

        // Assert
        assertNotNull(respuesta);
        assertFalse(respuesta.isEmpty());
        assertEquals(1, respuesta.size());
        assertEquals(usuario.getId(), respuesta.get(0).getId());

        verify(usuarioRepository).findAll();
    }

    @Test
    void obtenerPorId_Exito() {
        // Arrange
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));

        // Act
        UsuarioRespuestaDTO respuesta = usuarioService.obtenerPorId(1L);

        // Assert
        assertNotNull(respuesta);
        assertEquals(usuario.getId(), respuesta.getId());

        verify(usuarioRepository).findById(anyLong());
    }

    @Test
    void obtenerPorId_UsuarioNoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.obtenerPorId(1L));
        verify(usuarioRepository).findById(anyLong());
    }

    @Test
    void eliminarUsuario_Exito() {
        // Arrange
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.eliminarUsuario(1L);

        // Assert
        assertEquals("INACTIVO", usuario.getEstado());
        verify(usuarioRepository).findById(anyLong());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void eliminarUsuario_UsuarioNoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}