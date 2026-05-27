package com.fullstack.configuracion.service;

import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.exception.ClaveInvalidaException;
import com.fullstack.configuracion.exception.ConfigNoEncontradaException;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.repository.ConfiguracionRepository;
import com.fullstack.configuracion.repository.FeriadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;
    private final FeriadoRepository feriadoRepository;

    // Lista estricta de claves permitidas [cite: 645-646]
    private static final List<String> CLAVES_VALIDAS = Arrays.asList(
            "HORARIO_APERTURA", "HORARIO_CIERRE", "PRECIO_BASE_CANCHA"
    );

    @Transactional
    public ConfiguracionGlobal actualizarConfiguracion(String clave, ConfiguracionRequestDTO dto) {
        if (!CLAVES_VALIDAS.contains(clave.toUpperCase())) {
            throw new ClaveInvalidaException("La clave " + clave + " no es reconocida por el sistema.");
        }

        ConfiguracionGlobal config = configuracionRepository.findByClave(clave.toUpperCase())
                .orElseThrow(() -> new ConfigNoEncontradaException("Configuración no encontrada para la clave: " + clave));

        config.setValor(dto.getValor());
        config.setDescripcion(dto.getDescripcion());
        config.setFechaModificacion(LocalDateTime.now());
        config.setUsuarioId(dto.getUsuarioId());

        // Aquí iría la llamada al WebClient para propagar a ms-reservas y ms-pos [cite: 610-612]

        return configuracionRepository.save(config);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionGlobal> listarConfiguraciones() {
        return configuracionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ConfiguracionGlobal obtenerPorClave(String clave) {
        return configuracionRepository.findByClave(clave.toUpperCase())
                .orElseThrow(() -> new ConfigNoEncontradaException("Configuración no encontrada para la clave: " + clave));
    }

    @Transactional
    public FeriadoBloqueo registrarFeriado(FeriadoRequestDTO dto) {
        FeriadoBloqueo feriado = FeriadoBloqueo.builder()
                .fecha(dto.getFecha())
                .motivo(dto.getMotivo())
                .afectaReservas(dto.getAfectaReservas())
                .build();
        return feriadoRepository.save(feriado);
    }

    @Transactional(readOnly = true)
    public List<FeriadoBloqueo> listarFeriados() {
        return feriadoRepository.findAll();
    }

    @Transactional
    public void eliminarFeriado(Long id) {
        feriadoRepository.deleteById(id);
    }
}