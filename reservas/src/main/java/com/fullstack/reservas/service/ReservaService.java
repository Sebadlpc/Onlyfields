package com.fullstack.reservas.service;

import com.fullstack.reservas.client.AccesosClient;
import com.fullstack.reservas.client.NotificacionClient;
import com.fullstack.reservas.client.PosClient;
import com.fullstack.reservas.client.UsuarioClient;
import com.fullstack.reservas.dto.ReservaRequestDTO;
import com.fullstack.reservas.dto.ReservaResponseDTO;
import com.fullstack.reservas.exception.ConflictoReservaException;
import com.fullstack.reservas.exception.ReservaNoEncontradaException;
import com.fullstack.reservas.exception.ValidacionReservaException;
import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.models.Reserva;
import com.fullstack.reservas.repository.BloqueHorarioRepository;
import com.fullstack.reservas.repository.CanchaRepository;
import com.fullstack.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final CanchaRepository canchaRepository;
    private final BloqueHorarioRepository bloqueHorarioRepository;
    private final UsuarioClient usuarioClient;
    private final PosClient posClient;
    private final AccesosClient accesosClient;
    private final NotificacionClient notificacionClient;

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        // 1. Validar Usuario (Comunicación Feign)
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            throw new ValidacionReservaException("El cliente con ID " + dto.getClienteId() + " no existe o no está activo.");
        }

        // 2. Validar duración
        long minutos = Duration.between(dto.getFechaInicio(), dto.getFechaFin()).toMinutes();
        if (minutos < 60 || minutos > 240) {
            throw new ValidacionReservaException("La reserva debe durar entre 1 y 4 horas.");
        }

        // 3. Validar cancha
        Cancha cancha = canchaRepository.findById(dto.getCanchaId())
            .orElseThrow(() -> new ReservaNoEncontradaException("Cancha no encontrada con ID: " + dto.getCanchaId()));
        if (!"DISPONIBLE".equalsIgnoreCase(cancha.getEstado())) {
            throw new ConflictoReservaException("La cancha no está DISPONIBLE.");
        }

        // 4. Validar cruces (mantenimiento y otras reservas)
        if (!bloqueHorarioRepository.buscarChoques(cancha.getId(), dto.getFechaInicio(), dto.getFechaFin()).isEmpty()) {
            throw new ConflictoReservaException("La cancha está bloqueada por mantenimiento en ese horario.");
        }
        if (!reservaRepository.findReservasSolapadas(cancha.getId(), dto.getFechaInicio(), dto.getFechaFin()).isEmpty()) {
            throw new ConflictoReservaException("La cancha ya está reservada en ese horario.");
        }

        // 5. Calcular total y crear entidad
        BigDecimal horas = BigDecimal.valueOf(minutos).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setClienteId(dto.getClienteId());
        nuevaReserva.setCancha(cancha);
        nuevaReserva.setFechaInicio(dto.getFechaInicio());
        nuevaReserva.setFechaFin(dto.getFechaFin());
        nuevaReserva.setTotalCobrado(horas.multiply(cancha.getTarifaHora()));
        nuevaReserva.setEstado("PENDIENTE_PAGO");

        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);

        // 6. Enviar el monto al POS para generar el cobro
        try {
            posClient.generarCobro(new PosClient.CobroReservaDTO(
                reservaGuardada.getId(), 
                reservaGuardada.getClienteId(), 
                reservaGuardada.getTotalCobrado()
            ));
            log.info("Cobro generado en POS para reserva {}", reservaGuardada.getId());
        } catch (Exception e) {
            log.error("Error al generar cobro en POS para reserva {}: {}", reservaGuardada.getId(), e.getMessage());
        }

        return mapToDTO(reservaGuardada);
    }

    @Transactional
    public ReservaResponseDTO cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada con ID: " + id));
        
        long horasFaltantes = ChronoUnit.HOURS.between(LocalDateTime.now(), reserva.getFechaInicio());
        
        if (horasFaltantes < 24) {
            BigDecimal penalizacion = reserva.getTotalCobrado().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            reserva.setTotalCobrado(penalizacion);
        } else {
            reserva.setTotalCobrado(BigDecimal.ZERO);
        }

        reserva.setEstado("CANCELADA");
        return mapToDTO(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada con ID: " + id));
        
        if (!"PENDIENTE_PAGO".equals(reserva.getEstado())) {
            throw new ValidacionReservaException("Solo se pueden confirmar reservas con estado PENDIENTE_PAGO.");
        }
        
        reserva.setEstado("CONFIRMADA");
        Reserva reservaConfirmada = reservaRepository.save(reserva);

        // Informar a accesos que el QR es válido (Generando un código QR único)
        String qrCode = UUID.randomUUID().toString();
        try {
            accesosClient.registrarQrValido(new AccesosClient.QrReservaDTO(
                qrCode, 
                reservaConfirmada.getId(), 
                reservaConfirmada.getCancha().getId()
            ));
            log.info("QR registrado en Accesos para reserva {}", reservaConfirmada.getId());
        } catch (Exception e) {
            log.error("Error al registrar QR en Accesos para reserva {}: {}", reservaConfirmada.getId(), e.getMessage());
        }

        // Notificar al cliente
        try {
            // Asumimos que podemos obtener el email, o enviamos el ID para que notificaciones lo busque
            notificacionClient.enviarComprobante(new NotificacionClient.NotificacionReservaDTO(
                reservaConfirmada.getId(),
                "cliente_" + reservaConfirmada.getClienteId() + "@onlyfields.com", // Placeholder
                "Tu reserva ha sido CONFIRMADA. Presenta este QR en el acceso: " + qrCode
            ));
            log.info("Notificación de confirmación enviada para reserva {}", id);
        } catch (Exception e) {
            log.error("Fallo al enviar notificación para reserva {}: {}", id, e.getMessage());
        }
        
        return mapToDTO(reservaConfirmada);
    }

    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    @Transactional
    public void limpiarReservasExpiradas() {
        log.info("Ejecutando tarea: Limpieza de reservas expiradas...");
        LocalDateTime limite = LocalDateTime.now().minusMinutes(15);
        List<Reserva> expiradas = reservaRepository.buscarExpiradas("PENDIENTE_PAGO", limite);
        
        expiradas.forEach(reserva -> {
            reserva.setEstado("CANCELADA");
            reservaRepository.save(reserva);
            log.info("Reserva ID {} cancelada por expiración.", reserva.getId());
        });
        log.info("Tarea de limpieza finalizada. Se procesaron {} reservas.", expiradas.size());
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
            .map(this::mapToDTO)
            .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodasLasReservas() {
        return reservaRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ReservaResponseDTO mapToDTO(Reserva reserva) {
        return ReservaResponseDTO.builder()
            .id(reserva.getId())
            .clienteId(reserva.getClienteId())
            .canchaId(reserva.getCancha().getId())
            .nombreCancha(reserva.getCancha().getNombre())
            .fechaInicio(reserva.getFechaInicio())
            .fechaFin(reserva.getFechaFin())
            .estado(reserva.getEstado())
            .totalCobrado(reserva.getTotalCobrado())
            .build();
    }
}
