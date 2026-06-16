package com.fullstack.seguimiento.Service;

import com.fullstack.seguimiento.client.UsuarioClient;
import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.model.FichaCliente;
import com.fullstack.seguimiento.model.MedicionCorporal;
import com.fullstack.seguimiento.Repository.FichaClienteRepository;
import com.fullstack.seguimiento.Repository.MedicionCorporalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeguimientoService {

    private final FichaClienteRepository fichaRepository;
    private final MedicionCorporalRepository medicionRepository;
    private final UsuarioClient usuarioClient;

    @Transactional
    public FichaClienteDTO crearFicha(Long creadorId, FichaClienteDTO dto) {
        // 1. Validar que el creador es STAFF
        try {
            UsuarioClient.UsuarioResponseDTO creador = usuarioClient.obtenerUsuarioPorId(creadorId);
            if (creador == null || !"ROLE_STAFF".equalsIgnoreCase(creador.rolNombre()) && !"STAFF".equalsIgnoreCase(creador.rolNombre())) {
                throw new RuntimeException("Acceso denegado: Solo el personal de STAFF puede crear fichas clínicas.");
            }
        } catch (Exception e) {
            log.error("Error al validar rol del creador {}: {}", creadorId, e.getMessage());
            throw new RuntimeException("No se pudo validar los permisos del usuario creador.");
        }

        // 2. Validación local
        if (fichaRepository.existsByClienteId(dto.getClienteId())) {
            throw new RuntimeException("FichaYaExisteException: El cliente ya tiene una ficha clínica asignada.");
        }

        // 3. Validación Externa del Cliente (Feign)
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("UsuarioNotFoundException: No se puede crear la ficha porque el cliente con ID "
                    + dto.getClienteId() + " no existe en el sistema.");
        }

        // 4. Mapeo de DTO a Entidad para guardar
        FichaCliente ficha = FichaCliente.builder()
                .clienteId(dto.getClienteId())
                .lesionesPrevias(dto.getLesionesPrevias())
                .observaciones(dto.getObservaciones())
                .antecedentesMedicos(dto.getAntecedentesMedicos())
                .build();

        if (dto.getMediciones() != null && !dto.getMediciones().isEmpty()) {
            List<MedicionCorporal> mediciones = dto.getMediciones().stream()
                    .map(mDto -> MedicionCorporal.builder()
                            .peso(mDto.getPeso())
                            .altura(mDto.getAltura())
                            .perimetroCintura(mDto.getPerimetroCintura())
                            .perimetroCadera(mDto.getPerimetroCadera())
                            .objetivoActual(mDto.getObjetivoActual())
                            .porcentajeGrasa(mDto.getPorcentajeGrasa())
                            .masaMuscular(mDto.getMasaMuscular())
                            .fichaCliente(ficha)
                            .build())
                    .collect(Collectors.toList());
            ficha.setMediciones(mediciones);
        }

        FichaCliente fichaGuardada = fichaRepository.save(ficha);
        return mapearFichaADTO(fichaGuardada);
    }

    public FichaClienteDTO obtenerFichaPorCliente(Long clienteId) {
        FichaCliente ficha = fichaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: No se encontró ficha para este cliente."));
        return mapearFichaADTO(ficha);
    }

    @Transactional
    public FichaClienteDTO actualizarFicha(Long id, FichaClienteDTO dtoActualizada) {
        FichaCliente ficha = fichaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: Ficha no encontrada."));

        ficha.setLesionesPrevias(dtoActualizada.getLesionesPrevias());
        ficha.setObservaciones(dtoActualizada.getObservaciones());
        ficha.setAntecedentesMedicos(dtoActualizada.getAntecedentesMedicos());

        return mapearFichaADTO(fichaRepository.save(ficha));
    }

    @Transactional
    public void eliminarFicha(Long id) {
        if (!fichaRepository.existsById(id)) {
            throw new RuntimeException("FichaNotFoundException: Ficha no encontrada.");
        }
        fichaRepository.deleteById(id);
    }

    @Transactional
    public MedicionCorporalDTO agregarMedicion(Long fichaId, MedicionCorporalDTO mDto) {
        FichaCliente ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: Ficha no encontrada para asignar la medición."));

        MedicionCorporal medicion = MedicionCorporal.builder()
                .fichaCliente(ficha)
                .peso(mDto.getPeso())
                .altura(mDto.getAltura())
                .perimetroCintura(mDto.getPerimetroCintura())
                .perimetroCadera(mDto.getPerimetroCadera())
                .objetivoActual(mDto.getObjetivoActual())
                .porcentajeGrasa(mDto.getPorcentajeGrasa())
                .masaMuscular(mDto.getMasaMuscular())
                .build();

        return mapearMedicionADTO(medicionRepository.save(medicion));
    }

    public List<MedicionCorporalDTO> obtenerHistorialMediciones(Long fichaId) {
        if (!fichaRepository.existsById(fichaId)) {
            throw new RuntimeException("FichaNotFoundException: Ficha no encontrada.");
        }
        return medicionRepository.findByFichaClienteIdOrderByFechaMedicionDesc(fichaId).stream()
                .map(this::mapearMedicionADTO)
                .collect(Collectors.toList());
    }

    private FichaClienteDTO mapearFichaADTO(FichaCliente ficha) {
        if (ficha == null) return null;

        List<MedicionCorporalDTO> medicionesDto = null;
        if (ficha.getMediciones() != null) {
            medicionesDto = ficha.getMediciones().stream()
                    .map(this::mapearMedicionADTO)
                    .collect(Collectors.toList());
        }

        return new FichaClienteDTO(
                ficha.getId(),
                ficha.getClienteId(),
                ficha.getAntecedentesMedicos(),
                ficha.getLesionesPrevias(),
                ficha.getObservaciones(),
                ficha.getFechaCreacion(),
                medicionesDto
        );
    }

    private MedicionCorporalDTO mapearMedicionADTO(MedicionCorporal medicion) {
        if (medicion == null) return null;
        
        Double imc = null;
        if (medicion.getPeso() != null && medicion.getAltura() != null && medicion.getAltura() > 0) {
            imc = medicion.getPeso() / Math.pow(medicion.getAltura(), 2);
        }

        return new MedicionCorporalDTO(
                medicion.getId(),
                medicion.getFichaCliente() != null ? medicion.getFichaCliente().getId() : null,
                medicion.getFechaMedicion(),
                medicion.getPeso(),
                medicion.getAltura(),
                medicion.getPorcentajeGrasa(),
                medicion.getMasaMuscular(),
                medicion.getPerimetroCintura(),
                medicion.getPerimetroCadera(),
                imc,
                medicion.getObjetivoActual()
        );
    }
}
