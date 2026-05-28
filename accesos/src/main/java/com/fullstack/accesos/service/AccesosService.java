package com.fullstack.accesos.service;

import com.fullstack.accesos.client.ReservasClient;
import com.fullstack.accesos.client.SuscripcionesClient;
import com.fullstack.accesos.client.UsuarioClient;
import com.fullstack.accesos.dto.external.UsuarioExternoDTO;
import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.model.TipoAcceso;
import com.fullstack.accesos.repository.QrTokenRepository;
import com.fullstack.accesos.repository.RegistroAccesoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccesosService {

    private final QrTokenRepository qrTokenRepository;
    private final RegistroAccesoRepository registroAccesoRepository;
    private final UsuarioClient usuarioClient;
    private final SuscripcionesClient suscripcionesClient;
    private final ReservasClient reservasClient;

    @Transactional
    public QrToken generarQr(Long clienteId) {
        log.info("[ms-accesos] Consultando estado del usuario externo ID: {}", clienteId);

        UsuarioExternoDTO usuario;
        try {
            usuario = usuarioClient.obtenerUsuarioPorId(clienteId);
        } catch (Exception e) {
            log.error("[ms-accesos] Error de comunicación con ms-usuarios: {}", e.getMessage());
            throw new RuntimeException("El servicio de usuarios no está disponible.");
        }

        if (usuario == null || !"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new RuntimeException("El usuario no se encuentra ACTIVO para generar un QR.");
        }

        QrToken nuevoToken = QrToken.builder()
                .clienteId(clienteId)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusDays(1))
                .usado(false)
                .build();

        return qrTokenRepository.save(nuevoToken);
    }

    @Transactional
    public RegistroAcceso validarEntrada(String tokenString) {
        Optional<QrToken> tokenOpt = qrTokenRepository.findByToken(tokenString);

        RegistroAcceso registro = RegistroAcceso.builder()
                .tipo(TipoAcceso.ENTRADA)
                .build();

        if (tokenOpt.isEmpty()) {
            registro.setClienteId(0L);
            registro.setResultado(ResultadoAcceso.DENEGADO);
            registro.setMotivoRechazo("QR inválido o inexistente");
            return registroAccesoRepository.save(registro);
        }

        QrToken token = tokenOpt.get();
        registro.setClienteId(token.getClienteId());

        if (token.isUsado()) {
            registro.setResultado(ResultadoAcceso.DENEGADO);
            registro.setMotivoRechazo("El QR ya fue utilizado");
        } else if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            registro.setResultado(ResultadoAcceso.DENEGADO);
            registro.setMotivoRechazo("El QR ha expirado");
        } else {
            // Lógica de negocio: Validar suscripción o reserva
            boolean tieneAcceso = validarAccesoCliente(token.getClienteId());
            if (tieneAcceso) {
                registro.setResultado(ResultadoAcceso.PERMITIDO);
                token.setUsado(true);
                qrTokenRepository.save(token);
            } else {
                registro.setResultado(ResultadoAcceso.DENEGADO);
                registro.setMotivoRechazo("El cliente no tiene una suscripción activa ni una reserva para el día de hoy.");
            }
        }

        return registroAccesoRepository.save(registro);
    }

    private boolean validarAccesoCliente(Long clienteId) {
        // 1. Validar si tiene suscripción activa
        try {
            List<SuscripcionesClient.SuscripcionDTO> suscripciones = suscripcionesClient.obtenerSuscripcionesPorCliente(clienteId);
            boolean tieneSuscripcionActiva = suscripciones.stream().anyMatch(s -> "ACTIVA".equalsIgnoreCase(s.estado()));
            if (tieneSuscripcionActiva) {
                log.info("Acceso permitido para cliente {} por suscripción activa.", clienteId);
                return true;
            }
        } catch (Exception e) {
            log.error("Error al consultar ms-suscripciones para cliente {}: {}", clienteId, e.getMessage());
        }

        // 2. Si no tiene suscripción, validar si tiene reserva para hoy
        try {
            List<ReservasClient.ReservaDTO> reservas = reservasClient.obtenerReservasPorCliente(clienteId);
            LocalDateTime ahora = LocalDateTime.now();
            boolean tieneReservaHoy = reservas.stream().anyMatch(r -> 
                "CONFIRMADA".equalsIgnoreCase(r.estado()) &&
                r.fechaInicio().toLocalDate().isEqual(ahora.toLocalDate()) &&
                ahora.isBefore(r.fechaFin())
            );
            if (tieneReservaHoy) {
                log.info("Acceso permitido para cliente {} por reserva vigente.", clienteId);
                return true;
            }
        } catch (Exception e) {
            log.error("Error al consultar ms-reservas para cliente {}: {}", clienteId, e.getMessage());
        }

        log.warn("Acceso denegado para cliente {}. No tiene suscripción ni reserva.", clienteId);
        return false;
    }

    @Transactional
    public RegistroAcceso registrarSalida(Long clienteId) {
        RegistroAcceso registro = RegistroAcceso.builder()
                .clienteId(clienteId)
                .tipo(TipoAcceso.SALIDA)
                .resultado(ResultadoAcceso.PERMITIDO)
                .build();
        return registroAccesoRepository.save(registro);
    }

    public List<RegistroAcceso> obtenerHistorial() {
        return registroAccesoRepository.findAll();
    }

    public List<RegistroAcceso> obtenerHistorialCliente(Long clienteId) {
        return registroAccesoRepository.findByClienteIdOrderByFechaHoraDesc(clienteId);
    }

    public List<RegistroAcceso> obtenerActivos() {
        log.info("[ms-accesos] Buscando usuarios dentro del complejo");
        List<RegistroAcceso> historialCompleto = registroAccesoRepository.findAll();
        List<RegistroAcceso> usuariosActivos = new java.util.ArrayList<>();
        for (RegistroAcceso registro : historialCompleto) {
            if (registro.getClienteId() == null || registro.getClienteId() == 0) {
                continue;
            }
            if (registro.getTipo() == TipoAcceso.ENTRADA && registro.getResultado() == ResultadoAcceso.PERMITIDO) {
                usuariosActivos.add(registro);
            }
            else if (registro.getTipo() == TipoAcceso.SALIDA) {
                usuariosActivos.removeIf(r -> r.getClienteId().equals(registro.getClienteId()));
            }
        }
        return usuariosActivos;
    }
}
