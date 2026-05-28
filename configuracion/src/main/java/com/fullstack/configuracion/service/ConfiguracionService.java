package com.fullstack.configuracion.service;

import com.fullstack.configuracion.client.PosClient;
import com.fullstack.configuracion.client.ReservasClient;
import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.exception.ClaveInvalidaException;
import com.fullstack.configuracion.exception.ConfigNoEncontradaException;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.repository.ConfiguracionRepository;
import com.fullstack.configuracion.repository.FeriadoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;
    private final FeriadoRepository feriadoRepository;
    private final ReservasClient reservasClient;
    private final PosClient posClient;

    private static final Set<String> CLAVES_VALIDAS = Set.of(
            "HORARIO_APERTURA", "HORARIO_CIERRE", "PRECIO_BASE_CANCHA"
    );

    @Transactional
    public ConfiguracionGlobal actualizarConfiguracion(String clave, ConfiguracionRequestDTO dto) {
        String claveUpperCase = clave.toUpperCase();
        if (!CLAVES_VALIDAS.contains(claveUpperCase)) {
            throw new ClaveInvalidaException("La clave '" + clave + "' no es una clave de configuración válida.");
        }

        ConfiguracionGlobal config = configuracionRepository.findByClave(claveUpperCase)
                .orElseThrow(() -> new ConfigNoEncontradaException("Configuración no encontrada para la clave: " + clave));

        config.setValor(dto.getValor());
        config.setDescripcion(dto.getDescripcion());
        config.setFechaModificacion(LocalDateTime.now());
        config.setUsuarioId(dto.getUsuarioId());

        ConfiguracionGlobal savedConfig = configuracionRepository.save(config);

        // Propagar cambios a otros microservicios
        propagarCambios(savedConfig);

        return savedConfig;
    }

    private void propagarCambios(ConfiguracionGlobal config) {
        try {
            if ("HORARIO_APERTURA".equals(config.getClave()) || "HORARIO_CIERRE".equals(config.getClave())) {
                // En un escenario real, buscaríamos ambas claves de la BD para enviarlas juntas.
                // Aquí simulamos enviando el cambio actual.
                String apertura = "HORARIO_APERTURA".equals(config.getClave()) ? config.getValor() : "09:00"; // Default fallback
                String cierre = "HORARIO_CIERRE".equals(config.getClave()) ? config.getValor() : "22:00";     // Default fallback
                
                reservasClient.actualizarHorario(new ReservasClient.HorarioDTO(apertura, cierre));
                log.info("Cambio de horario propagado a ms-reservas");
            } else if ("PRECIO_BASE_CANCHA".equals(config.getClave())) {
                posClient.actualizarTarifa(new PosClient.TarifaDTO(config.getClave(), new BigDecimal(config.getValor())));
                log.info("Cambio de tarifa propagado a ms-pos");
            }
        } catch (Exception e) {
            log.error("Error al propagar configuración a otros servicios: {}", e.getMessage());
            // Dependiendo del requisito de negocio, podríamos lanzar una excepción o simplemente loguear el error.
        }
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
        if (!feriadoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Feriado no encontrado con ID: " + id);
        }
        feriadoRepository.deleteById(id);
    }
}
