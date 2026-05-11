package com.fullstack.reservas.service;

import com.fullstack.reservas.models.Reserva;
import java.util.List;

public interface IReservaService {

    Reserva crearReserva(Reserva reserva);

    Reserva obtenerReservaPorId(Long id);

    List<Reserva> obtenerTodasLasReservas();

    Reserva actualizarReserva(Long id, Reserva reserva);

    void eliminarReserva(Long id);
}