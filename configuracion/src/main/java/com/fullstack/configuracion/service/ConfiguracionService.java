package com.fullstack.configuracion.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio que encapsula la lógica de negocio para la gestión de configuraciones y feriados.
 */
@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;
    private final FeriadoRepository feriadoRepository;

    // Conjunto estricto de claves de configuración permitidas para evitar la creación de claves arbitrarias.
    private static final Set<String> CLAVES_VALIDAS = Set.of(
            "HORARIO_APERTURA", "HORARIO_CIERRE", "PRECIO_BASE_CANCHA"
    );

    /**
     * Actualiza una configuración existente. Valida que la clave sea una de las permitidas.
     * @param clave La clave de la configuración a actualizar.
     * @param dto DTO con los nuevos datos.
     * @return La configuración actualizada.
     * @throws ClaveInvalidaException si la clave no está en la lista de claves válidas.
     * @throws ConfigNoEncontradaException si no se encuentra una configuración con esa clave.
     */
    @Transactional
    public ConfiguracionGlobal actualizarConfiguracion(String clave, ConfiguracionRequestDTO dto) {
        String claveUpperCase = clave.toUpperCase();
        if (!CLAVES_VALIDAS.contains(claveUpperCase)) {
            throw new ClaveInvalidaException("La clave '" + clave + "' no es una clave de configuración válida.");
        }

        ConfiguracionGlobal config = configuracionRepository.findByClave(claveUpperCase)
                .orElseThrow(() -> new ConfigNoEncontradaException("Configuración no encontrada para la clave: " + clave));

        // Actualiza los campos de la entidad
        config.setValor(dto.getValor());
        config.setDescripcion(dto.getDescripcion());
        config.setFechaModificacion(LocalDateTime.now());
        config.setUsuarioId(dto.getUsuarioId());

        // TODO: Implementar la llamada al WebClient para propagar el cambio a otros microservicios

        return configuracionRepository.save(config);
    }

    /**
     * Obtiene todas las configuraciones globales del sistema.
     */
    @Transactional(readOnly = true)
    public List<ConfiguracionGlobal> listarConfiguraciones() {
        return configuracionRepository.findAll();
    }

    /**
     * Busca y devuelve una configuración por su clave.
     * @param clave La clave de la configuración.
     * @throws ConfigNoEncontradaException si la configuración no existe.
     */
    @Transactional(readOnly = true)
    public ConfiguracionGlobal obtenerPorClave(String clave) {
        return configuracionRepository.findByClave(clave.toUpperCase())
                .orElseThrow(() -> new ConfigNoEncontradaException("Configuración no encontrada para la clave: " + clave));
    }

    /**
     * Registra un nuevo día feriado o de bloqueo.
     * @param dto DTO con los datos del feriado.
     */
    @Transactional
    public FeriadoBloqueo registrarFeriado(FeriadoRequestDTO dto) {
        FeriadoBloqueo feriado = FeriadoBloqueo.builder()
                .fecha(dto.getFecha())
                .motivo(dto.getMotivo())
                .afectaReservas(dto.getAfectaReservas())
                .build();
        return feriadoRepository.save(feriado);
    }

    /**
     * Obtiene todos los feriados o días de bloqueo registrados.
     */
    @Transactional(readOnly = true)
    public List<FeriadoBloqueo> listarFeriados() {
        return feriadoRepository.findAll();
    }

    /**
     * Elimina un feriado de la base de datos.
     * @param id El ID del feriado a eliminar.
     * @throws EntityNotFoundException si el feriado no existe.
     */
    @Transactional
    public void eliminarFeriado(Long id) {
        if (!feriadoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Feriado no encontrado con ID: " + id);
        }
        feriadoRepository.deleteById(id);
    }
}
