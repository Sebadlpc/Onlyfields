package com.fullstack.reservas.service.impl;

import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.models.Reserva;
import com.fullstack.reservas.repository.CanchaRepository;
import com.fullstack.reservas.repository.*;
import com.fullstack.reservas.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
public class ReservaServiceImpl implements IReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    // ---------------------------------------------------------------
    // CREAR
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public Reserva crearReserva(Reserva reserva) {

        // 1. Validar que fechaFin sea posterior a fechaInicio
        if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())) {
            throw new RuntimeException(
                "La fecha de fin debe ser posterior a la fecha de inicio."
            );
        }

        // 2. Cargar la Cancha completa desde la BD para acceder a tarifaHora
        Cancha cancha = canchaRepository.findById(reserva.getCancha().getId())
            .orElseThrow(() -> new RuntimeException(
                "No se encontró la cancha con ID: " + reserva.getCancha().getId()
            ));

        // 3. Calcular duración en horas (fraccionadas) y totalCobrado
        long minutos = Duration.between(
            reserva.getFechaInicio(),
            reserva.getFechaFin()
        ).toMinutes();

        BigDecimal horas        = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60));
        BigDecimal totalCobrado = horas.multiply(cancha.getTarifaHora());

        // 4. Establecer valores calculados / por defecto
        reserva.setCancha(cancha);
        reserva.setTotalCobrado(totalCobrado);
        reserva.setEstado("CONFIRMADA");

        return reservaRepository.save(reserva);
    }

    // ---------------------------------------------------------------
    // OBTENER POR ID
    // ---------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                "No se encontró la reserva con ID: " + id
            ));
    }

    // ---------------------------------------------------------------
    // LISTAR TODAS
    // ---------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    // ---------------------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public Reserva actualizarReserva(Long id, Reserva datosNuevos) {

        // Verificar que la reserva existe
        Reserva reservaExistente = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                "No se encontró la reserva con ID: " + id
            ));

        // Validar fechas si se proporcionan nuevas
        if (!datosNuevos.getFechaFin().isAfter(datosNuevos.getFechaInicio())) {
            throw new RuntimeException(
                "La fecha de fin debe ser posterior a la fecha de inicio."
            );
        }

        // Cargar la cancha actualizada si cambia
        Cancha cancha = canchaRepository.findById(datosNuevos.getCancha().getId())
            .orElseThrow(() -> new RuntimeException(
                "No se encontró la cancha con ID: " + datosNuevos.getCancha().getId()
            ));

        // Recalcular totalCobrado con los nuevos datos
        long minutos = Duration.between(
            datosNuevos.getFechaInicio(),
            datosNuevos.getFechaFin()
        ).toMinutes();

        BigDecimal horas        = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60));
        BigDecimal totalCobrado = horas.multiply(cancha.getTarifaHora());

        // Aplicar cambios sobre la entidad gestionada por JPA
        reservaExistente.setCancha(cancha);
        reservaExistente.setClienteId(datosNuevos.getClienteId());
        reservaExistente.setFechaInicio(datosNuevos.getFechaInicio());
        reservaExistente.setFechaFin(datosNuevos.getFechaFin());
        reservaExistente.setEstado(datosNuevos.getEstado());
        reservaExistente.setTotalCobrado(totalCobrado);

        return reservaRepository.save(reservaExistente);
    }

    // ---------------------------------------------------------------
    // ELIMINAR
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public void eliminarReserva(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException(
                "No se puede eliminar: no se encontró la reserva con ID: " + id
            );
        }
        reservaRepository.deleteById(id);
    }
}