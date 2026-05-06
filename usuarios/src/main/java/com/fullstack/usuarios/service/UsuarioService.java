package com.fullstack.usuarios.service;

import com.fullstack.usuarios.exception.UsuarioNoEncontradoException;
import com.fullstack.usuarios.model.Usuario;
import com.fullstack.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())) {
            log.warn("[ms-usuarios] Validacion fallida: Email duplicado {}", usuario.getCorreoElectronico());
            throw new RuntimeException("El correo electronico ya esta registrado");
        }
        // Inicializar campos requeridos por el modelo
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setEstado("ACTIVO");
        log.info("[ms-usuarios] Operacion iniciada: Creando usuario {}", usuario.getNombre());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario datosActualizados) {
        Usuario usuarioExistente = obtenerPorId(id);
        usuarioExistente.setNombre(datosActualizados.getNombre());
        usuarioExistente.setEstado(datosActualizados.getEstado());
        // No se permite cambiar el email en esta version segun reglas de negocio
        return usuarioRepository.save(usuarioExistente);
    }

@Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        // Segun el informe, se prefiere restringir/desactivar
        usuario.setEstado("INACTIVO");
        usuarioRepository.save(usuario);
        log.info("[ms-usuarios] Usuario con ID {} marcado como INACTIVO", id);
    }

    @Transactional
    public void actualizarPassword(Long id, String nuevoPassword) {
        Usuario usuario = obtenerPorId(id);
        usuario.setPasswordHash(nuevoPassword);
        usuarioRepository.save(usuario);
        log.info("[ms-usuarios] Password actualizado para usuario con ID {}", id);
    }
}
