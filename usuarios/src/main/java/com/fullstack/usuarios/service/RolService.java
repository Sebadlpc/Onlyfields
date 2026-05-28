package com.fullstack.usuarios.service;

import com.fullstack.usuarios.dto.RolDTO;
import com.fullstack.usuarios.exception.EmailYaRegistradoException;
import com.fullstack.usuarios.exception.RolConUsuariosException;
import com.fullstack.usuarios.exception.RolNoEncontradoException;
import com.fullstack.usuarios.model.Rol;
import com.fullstack.usuarios.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que encapsula la lógica de negocio para la gestión de roles.
 */
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    /**
     * Crea un nuevo rol en el sistema.
     * El nombre del rol se convierte a mayúsculas para mantener la consistencia.
     * @param dto DTO con los datos del rol a crear.
     * @return El DTO del rol recién creado.
     * @throws EmailYaRegistradoException si ya existe un rol con el mismo nombre.
     */
    @Transactional
    public RolDTO crearRol(RolDTO dto) {
        String nombreRol = dto.getNombre().toUpperCase();
        if (rolRepository.findByNombre(nombreRol).isPresent()) {
            throw new EmailYaRegistradoException("El rol '" + nombreRol + "' ya existe.");
        }

        Rol rol = Rol.builder()
                .nombre(nombreRol)
                .descripcion(dto.getDescripcion())
                .build();

        Rol rolGuardado = rolRepository.save(rol);
        return mapearADTO(rolGuardado);
    }

    /**
     * Obtiene una lista de todos los roles disponibles en el sistema.
     * @return Una lista de DTOs de roles.
     */
    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de un rol existente.
     * @param id El ID del rol a actualizar.
     * @param dto DTO con la nueva información.
     * @return El DTO del rol actualizado.
     * @throws RolNoEncontradoException si no se encuentra un rol con el ID proporcionado.
     */
    @Transactional
    public RolDTO actualizarRol(Long id, RolDTO dto) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado con ID: " + id));

        rol.setNombre(dto.getNombre().toUpperCase());
        rol.setDescripcion(dto.getDescripcion());

        Rol rolActualizado = rolRepository.save(rol);
        return mapearADTO(rolActualizado);
    }

    /**
     * Elimina un rol del sistema.
     * No se puede eliminar un rol si tiene usuarios asignados.
     * @param id El ID del rol a eliminar.
     * @throws RolNoEncontradoException si el rol no existe.
     * @throws RolConUsuariosException si el rol tiene usuarios asociados.
     */
    @Transactional
    public void eliminarRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado con ID: " + id));

        if (!rol.getUsuarios().isEmpty()) {
            throw new RolConUsuariosException("No se puede eliminar el rol '" + rol.getNombre() + "' porque tiene " + rol.getUsuarios().size() + " usuario(s) asignado(s).");
        }

        rolRepository.delete(rol);
    }

    /**
     * Método de utilidad para convertir una entidad {@link Rol} a un {@link RolDTO}.
     * @param rol La entidad a convertir.
     * @return El DTO correspondiente.
     */
    private RolDTO mapearADTO(Rol rol) {
        return RolDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .build();
    }
}
