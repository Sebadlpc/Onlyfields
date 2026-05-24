package com.fullstack.reservas.service;

import com.fullstack.reservas.models.Reserva;
import java.util.List;

public interface IReservaService {
    Reserva crearReserva(Reserva reserva);
    Reserva obtenerReservaPorId(Long id);
    List<Reserva> obtenerTodasLasReservas();
    List<Reserva> obtenerPorCliente(Long clienteId);
    Reserva actualizarReserva(Long id, Reserva reserva);
    Reserva cancelarReserva(Long id); // 🌟 Nuevo método
    void eliminarReserva(Long id);
}