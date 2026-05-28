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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que encapsula la lógica de negocio para la gestión de usuarios y autenticación.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema.
     * @param dto DTO con los datos de registro.
     * @return DTO con la información del usuario creado.
     * @throws EmailYaRegistradoException si el correo electrónico ya está en uso.
     * @throws RolNoEncontradoException si el rol especificado no existe.
     */
    @Transactional
    public UsuarioRespuestaDTO registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getCorreoElectronico())) {
            throw new EmailYaRegistradoException("El correo electrónico '" + dto.getCorreoElectronico() + "' ya está registrado.");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RolNoEncontradoException("El rol con ID '" + dto.getRolId() + "' no es válido."));

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getCorreoElectronico())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .estado("ACTIVO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        usuario.agregarRol(rol);
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return mapearARespuestaDTO(nuevoUsuario);
    }

    /**
     * Valida las credenciales de un usuario para el login.
     * @param dto DTO con email y contraseña.
     * @return DTO con la información del usuario si el login es exitoso.
     * @throws UsuarioNoEncontradoException si no se encuentra un usuario con ese email.
     * @throws CredencialesInvalidasException si la contraseña es incorrecta o la cuenta está inactiva.
     */
    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO login(AuthLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getCorreoElectronico())
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró un usuario con el correo: " + dto.getCorreoElectronico()));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("La contraseña es incorrecta.");
        }
        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new CredencialesInvalidasException("La cuenta del usuario no está activa.");
        }
        return mapearARespuestaDTO(usuario);
    }

    /**
     * Obtiene una lista de todos los usuarios.
     * @return Lista de DTOs de usuario.
     */
    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearARespuestaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID.
     * @param id El ID del usuario.
     * @return DTO con la información del usuario.
     * @throws UsuarioNoEncontradoException si el usuario no existe.
     */
    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con el ID: " + id));
        return mapearARespuestaDTO(usuario);
    }

    /**
     * Realiza un borrado lógico de un usuario, cambiando su estado a "INACTIVO".
     * @param id El ID del usuario a desactivar.
     * @throws UsuarioNoEncontradoException si el usuario no existe.
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con el ID: " + id));
        usuario.setEstado("INACTIVO");
        usuarioRepository.save(usuario);
    }

    /**
     * Método de utilidad para convertir una entidad {@link Usuario} a un {@link UsuarioRespuestaDTO}.
     * @param usuario La entidad a convertir.
     * @return El DTO con los datos públicos del usuario.
     */
    private UsuarioRespuestaDTO mapearARespuestaDTO(Usuario usuario) {
        String rolNombre = usuario.getRoles().stream()
                .findFirst()
                .map(Rol::getNombre)
                .orElse("SIN_ROL");

        return UsuarioRespuestaDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correoElectronico(usuario.getEmail())
                .estado(usuario.getEstado())
                .fechaCreacion(usuario.getFechaCreacion())
                .rolNombre(rolNombre)
                .build();
    }
}
