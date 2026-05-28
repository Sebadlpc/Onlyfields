package com.fullstack.reservas.dto;

import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.models.Reserva;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ReservaRequestDTO {

    @NotNull(message = "El ID de la cancha es obligatorio")
    private Long canchaId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La reserva debe ser en una fecha futura")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    // Mapeo DTO
    // -----------------------------------------------------------------
    public Reserva toEntity() {
        Reserva reserva = new Reserva();

        // Solo asignamos el ID de la cancha
        Cancha cancha = new Cancha();
        cancha.setId(this.canchaId);

        reserva.setCancha(cancha);
        reserva.setClienteId(this.clienteId);
        reserva.setFechaInicio(this.fechaInicio);
        reserva.setFechaFin(this.fechaFin);

        return reserva;
    }
}