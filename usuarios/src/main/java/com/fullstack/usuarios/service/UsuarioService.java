package com.fullstack.usuarios.service;

import com.fullstack.usuarios.dto.DTONucleo.*;
import com.fullstack.usuarios.model.Rol;
import com.fullstack.usuarios.model.Usuario;
import com.fullstack.usuarios.repository.RolRepository;
import com.fullstack.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioRespuestaDTO registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getCorreoElectronico())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getCorreoElectronico())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .estado("ACTIVO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        usuario.agregarRol(rol);
        return mapearARespuestaDTO(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO login(AuthLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("Cuenta inactiva");
        }
        return mapearARespuestaDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::mapearARespuestaDTO).collect(Collectors.toList());
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado("INACTIVO"); // Borrado lógico
        usuarioRepository.save(usuario);
    }

    private UsuarioRespuestaDTO mapearARespuestaDTO(Usuario usuario) {
        String rol = (usuario.getRoles() != null && !usuario.getRoles().isEmpty())
                ? usuario.getRoles().iterator().next().getNombre() : "SIN_ROL";

        return UsuarioRespuestaDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correoElectronico(usuario.getEmail())
                .estado(usuario.getEstado())
                .fechaCreacion(usuario.getFechaCreacion())
                .rolNombre(rol)
                .build();
    }
}