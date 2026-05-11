package com.fullstack.usuarios.service;

import com.fullstack.usuarios.dto.AuthLoginDTO;
import com.fullstack.usuarios.dto.UsuarioRegistroDTO;
import com.fullstack.usuarios.dto.UsuarioRespuestaDTO;
import com.fullstack.usuarios.dto.UsuarioUpdateDTO;
import com.fullstack.usuarios.model.Rol;
import com.fullstack.usuarios.model.Usuario;
import com.fullstack.usuarios.repository.RolRepository;
import com.fullstack.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder; // Inyectamos BCrypt

    @Transactional
    public UsuarioRespuestaDTO registrarUsuario(UsuarioRegistroDTO dto) {
        log.info("[ms-usuarios] Iniciando registro para correo: {}", dto.getCorreoElectronico());

        if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            log.warn("[ms-usuarios] Validacion fallida: Email duplicado {}", dto.getCorreoElectronico());
            throw new RuntimeException("El correo electrónico ya está en uso"); 
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setCorreoElectronico(dto.getCorreoElectronico());
        
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(dto.getPassword())); 
        
        nuevoUsuario.setEstado("ACTIVO"); // Estado por defecto
        nuevoUsuario.setFechaCreacion(LocalDateTime.now());
        nuevoUsuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        log.info("[ms-usuarios] Operacion iniciada: Creado con ID {}", usuarioGuardado.getId());

        return mapearARespuestaDTO(usuarioGuardado);
    }

    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return mapearARespuestaDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearARespuestaDTO)
                .collect(Collectors.toList());
    }

    private UsuarioRespuestaDTO mapearARespuestaDTO(Usuario usuario) {
        return UsuarioRespuestaDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correoElectronico(usuario.getCorreoElectronico())
                .estado(usuario.getEstado())
                .fechaCreacion(usuario.getFechaCreacion())
                .rolNombre(usuario.getRol().getNombre()) // Sacamos el nombre del rol del objeto anidado
                .build();
    }


@Transactional
public UsuarioRespuestaDTO actualizarUsuario(Long id, UsuarioUpdateDTO dto) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (!usuario.getCorreoElectronico().equals(dto.getCorreoElectronico()) &&
        usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
        throw new RuntimeException("El correo electrónico ya está en uso");
    }

    usuario.setNombre(dto.getNombre());
    usuario.setCorreoElectronico(dto.getCorreoElectronico());
    if (dto.getEstado() != null) {
        usuario.setEstado(dto.getEstado());
    }

    return mapearARespuestaDTO(usuarioRepository.save(usuario));
}

@Transactional
public void eliminarUsuario(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    // Siguiendo la documentación, aplicamos borrado lógico para no romper integridad
    usuario.setEstado("INACTIVO");
    usuarioRepository.save(usuario);
}

@Transactional(readOnly = true)
public UsuarioRespuestaDTO login(AuthLoginDTO dto) {
    Usuario usuario = usuarioRepository.findByCorreoElectronico(dto.getCorreoElectronico())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

    if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
        throw new RuntimeException("Credenciales inválidas");
    }

    if (!"ACTIVO".equals(usuario.getEstado())) {
        throw new RuntimeException("La cuenta se encuentra inactiva");
    }

    return mapearARespuestaDTO(usuario);
}

}