package com.fullstack.usuarios.service;

import com.fullstack.usuarios.dto.RolDTO;
import com.fullstack.usuarios.model.Rol;
import com.fullstack.usuarios.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    @Transactional
    public RolDTO crearRol(RolDTO dto) {
        log.info("[ms-usuarios] Operacion iniciada: Creando rol {}", dto.getNombre());
        
        if (rolRepository.findByNombre(dto.getNombre().toUpperCase()).isPresent()) {
            throw new RuntimeException("El rol ya existe"); // Reemplazar por RolDuplicadoException
        }

        Rol rol = Rol.builder()
                .nombre(dto.getNombre().toUpperCase())
                .descripcion(dto.getDescripcion())
                .build();

        Rol rolGuardado = rolRepository.save(rol);
        return mapearADTO(rolGuardado);
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RolDTO actualizarRol(Long id, RolDTO dto) {
        log.info("[ms-usuarios] Operacion iniciada: Actualizando rol {}", id);
        
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        rol.setNombre(dto.getNombre().toUpperCase());
        rol.setDescripcion(dto.getDescripcion());

        return mapearADTO(rolRepository.save(rol));
    }

    @Transactional
    public void eliminarRol(Long id) {
        log.info("[ms-usuarios] Operacion iniciada: Eliminando rol {}", id);
        
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Validacion crítica de integridad referencial
        if (!rol.getUsuarios().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el rol porque tiene usuarios asignados");
        }

        rolRepository.delete(rol);
    }

    private RolDTO mapearADTO(Rol rol) {
        return RolDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .build();
    }
}