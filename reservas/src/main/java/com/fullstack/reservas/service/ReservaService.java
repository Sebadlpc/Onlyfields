package com.fullstack.reservas.service;

import com.fullstack.reservas.exception.ResourceNotFoundException;
import com.fullstack.reservas.model.Reserva;
import com.fullstack.reservas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Reserva actualizar(Long id, Reserva nuevaReserva) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setClienteId(nuevaReserva.getClienteId());
                    reserva.setServicioId(nuevaReserva.getServicioId());
                    reserva.setFechaReserva(nuevaReserva.getFechaReserva());
                    reserva.setHoraReserva(nuevaReserva.getHoraReserva());
                    reserva.setEstado(nuevaReserva.getEstado());
                    reserva.setObservaciones(nuevaReserva.getObservaciones());
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
    }

    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva no encontrada con ID: " + id);
        }

        reservaRepository.deleteById(id);
    }
}