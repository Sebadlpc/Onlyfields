package com.fullstack.reservas.service.impl;

import com.fullstack.reservas.client.NotificacionClient;
import com.fullstack.reservas.client.UsuarioClient;
import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.models.Reserva;
import com.fullstack.reservas.repository.*;
import com.fullstack.reservas.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class ReservaServiceImpl implements IReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private BloqueHorarioRepository bloqueHorarioRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private NotificacionClient notificacionClient;

    @Override
    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        // 1. Validar Usuario (Comunicación con ms-usuarios vía Feign)
        try {
            // Transformamos la respuesta de Feign a Map para leer el estado
            @SuppressWarnings("unchecked")
            Map<String, Object> usuario = (Map<String, Object>) usuarioClient.obtenerUsuarioPorId(reserva.getClienteId());

            if (usuario == null || !"ACTIVO".equals(usuario.get("estado"))) {
                throw new RuntimeException("El cliente no está ACTIVO o no existe.");
            }
        } catch (feign.FeignException.NotFound e) {
            throw new RuntimeException("El cliente con ID " + reserva.getClienteId() + " no existe en la base de datos.");
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al validar cliente: " + e.getMessage());
        }

        // 2. Validar duración de 1 a 4 horas
        long minutos = Duration.between(reserva.getFechaInicio(), reserva.getFechaFin()).toMinutes();
        if (minutos < 60 || minutos > 240) {
            throw new RuntimeException("La reserva debe durar entre 1 y 4 horas.");
        }

        // 3. Validar estado de la cancha
        Cancha cancha = canchaRepository.findById(reserva.getCancha().getId())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
        if (!"DISPONIBLE".equalsIgnoreCase(cancha.getEstado())) {
            throw new RuntimeException("La cancha no está DISPONIBLE.");
        }

        // 4. Validar cruce con Bloqueos de Mantenimiento
        if (!bloqueHorarioRepository.buscarChoques(cancha.getId(), reserva.getFechaInicio(), reserva.getFechaFin()).isEmpty()) {
            throw new RuntimeException("La cancha está bloqueada por mantenimiento en ese horario.");
        }

        // 5. Validar cruce con otras Reservas
        if (!reservaRepository.findReservasSolapadas(cancha.getId(), reserva.getFechaInicio(), reserva.getFechaFin()).isEmpty()) {
            throw new RuntimeException("La cancha ya está reservada en ese horario.");
        }

        // 6. Calcular total y establecer PENDIENTE_PAGO
        BigDecimal horas = BigDecimal.valueOf(minutos).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        reserva.setCancha(cancha);
        reserva.setTotalCobrado(horas.multiply(cancha.getTarifaHora()));
        reserva.setEstado("PENDIENTE_PAGO");

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = obtenerReservaPorId(id);

        long horasFaltantes = ChronoUnit.HOURS.between(LocalDateTime.now(), reserva.getFechaInicio());

        if (horasFaltantes < 24) {
            BigDecimal penalizacion = reserva.getTotalCobrado().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            reserva.setTotalCobrado(penalizacion);
        } else {
            reserva.setTotalCobrado(BigDecimal.ZERO);
        }

        reserva.setEstado("CANCELADA");
        return reservaRepository.save(reserva);
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void limpiarReservasExpiradas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(15);
        List<Reserva> expiradas = reservaRepository.buscarExpiradas("PENDIENTE_PAGO", limite);

        for (Reserva r : expiradas) {
            r.setEstado("CANCELADA");
            reservaRepository.save(r);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public Reserva actualizarReserva(Long id, Reserva datosNuevos) {
        Reserva r = obtenerReservaPorId(id);
        r.setFechaInicio(datosNuevos.getFechaInicio());
        r.setFechaFin(datosNuevos.getFechaFin());
        return reservaRepository.save(r);
    }

    @Override
    @Transactional
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Reserva confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado("CONFIRMADA");

        // Uso del cliente Feign en lugar del antiguo RestTemplate
        try {
            notificacionClient.enviarComprobante(new NotificacionClient.NotificacionReservaDTO(
                    reserva.getId(),
                    "cliente_" + reserva.getClienteId() + "@onlyfields.com",
                    "Tu reserva ha sido CONFIRMADA"
            ));
            System.out.println("✅ Notificación enviada con éxito vía Feign para la reserva " + id);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar notificación vía Feign, pero la reserva se confirmó: " + e.getMessage());
        }

        return reservaRepository.save(reserva);
    }
}