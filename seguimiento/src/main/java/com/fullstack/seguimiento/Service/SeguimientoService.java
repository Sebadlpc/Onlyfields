package com.fullstack.seguimiento.Service;

import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.Model.FichaCliente;
import com.fullstack.seguimiento.Model.MedicionCorporal;
import com.fullstack.seguimiento.Repository.FichaClienteRepository;
import com.fullstack.seguimiento.Repository.MedicionCorporalRepository;
import com.fullstack.seguimiento.client.UsuarioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeguimientoService {

    @Autowired
    private FichaClienteRepository fichaRepository;

    @Autowired
    private MedicionCorporalRepository medicionRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    // --- LÓGICA DE FICHAS ---

    @Transactional
    public FichaClienteDTO crearFicha(FichaClienteDTO dto) {
        // 1. Validación local
        if (fichaRepository.existsByClienteId(dto.getClienteId())) {
            throw new RuntimeException("FichaYaExisteException: El cliente ya tiene una ficha clínica asignada.");
        }

        // 2. Validación Externa (Feign)
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("UsuarioNotFoundException: No se puede crear la ficha porque el cliente con ID "
                    + dto.getClienteId() + " no existe en el sistema o el servicio no está disponible.");
        }

        // 3. Mapeo de DTO a Entidad para guardar
        FichaCliente ficha = FichaCliente.builder()
                .clienteId(dto.getClienteId())
                .lesiones(dto.getLesiones())
                .observaciones(dto.getObservaciones())
                .build();

        if (dto.getMediciones() != null && !dto.getMediciones().isEmpty()) {
            List<MedicionCorporal> mediciones = dto.getMediciones().stream()
                    .map(mDto -> MedicionCorporal.builder()
                            .peso(mDto.getPeso())
                            .altura(mDto.getAltura())
                            .cintura(mDto.getCintura())
                            .cadera(mDto.getCadera())
                            .objetivo(mDto.getObjetivo())
                            .ficha(ficha)
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

        ficha.setLesiones(dtoActualizada.getLesiones());
        ficha.setObservaciones(dtoActualizada.getObservaciones());

        return mapearFichaADTO(fichaRepository.save(ficha));
    }

    @Transactional
    public void eliminarFicha(Long id) {
        if (!fichaRepository.existsById(id)) {
            throw new RuntimeException("FichaNotFoundException: Ficha no encontrada.");
        }
        fichaRepository.deleteById(id);
    }

    // --- LÓGICA DE MEDICIONES ---

    @Transactional
    public MedicionCorporalDTO agregarMedicion(Long fichaId, MedicionCorporalDTO mDto) {
        FichaCliente ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: Ficha no encontrada para asignar la medición."));

        MedicionCorporal medicion = MedicionCorporal.builder()
                .ficha(ficha)
                .peso(mDto.getPeso())
                .altura(mDto.getAltura())
                .cintura(mDto.getCintura())
                .cadera(mDto.getCadera())
                .objetivo(mDto.getObjetivo())
                .build();

        return mapearMedicionADTO(medicionRepository.save(medicion));
    }

    public List<MedicionCorporalDTO> obtenerHistorialMediciones(Long fichaId) {
        if (!fichaRepository.existsById(fichaId)) {
            throw new RuntimeException("FichaNotFoundException: Ficha no encontrada.");
        }
        return medicionRepository.findByFichaIdOrderByFechaDesc(fichaId).stream()
                .map(this::mapearMedicionADTO)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS PRIVADOS DE MAPEO (MAPPERS) ---

    private FichaClienteDTO mapearFichaADTO(FichaCliente ficha) {
        if (ficha == null) return null;

        List<MedicionCorporalDTO> medicionesDto = null;
        if (ficha.getMediciones() != null) {
            medicionesDto = ficha.getMediciones().stream()
                    .map(this::mapearMedicionADTO)
                    .collect(Collectors.toList());
        }

        return FichaClienteDTO.builder()
                .id(ficha.getId())
                .clienteId(ficha.getClienteId())
                .lesiones(ficha.getLesiones())
                .observaciones(ficha.getObservaciones())
                .fechaIngreso(ficha.getFechaIngreso())
                .mediciones(medicionesDto)
                .build();
    }

    private MedicionCorporalDTO mapearMedicionADTO(MedicionCorporal medicion) {
        if (medicion == null) return null;
        return MedicionCorporalDTO.builder()
                .id(medicion.getId())
                .fichaId(medicion.getFicha() != null ? medicion.getFicha().getId() : null)
                .fecha(medicion.getFecha())
                .peso(medicion.getPeso())
                .altura(medicion.getAltura())
                .imc(medicion.getImc())
                .cintura(medicion.getCintura())
                .cadera(medicion.getCadera())
                .objetivo(medicion.getObjetivo())
                .build();
    }
}
