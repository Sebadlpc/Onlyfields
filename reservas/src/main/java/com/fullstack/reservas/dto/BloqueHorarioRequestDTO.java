package com.fullstack.reservas.dto;

import com.fullstack.reservas.models.BloqueHorario;
import com.fullstack.reservas.models.Cancha;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BloqueHorarioRequestDTO {

    @NotNull(message = "El ID de la cancha es obligatorio")
    private Long canchaId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    private String motivo;


    public BloqueHorario toEntity() {
        BloqueHorario bloque = new BloqueHorario();

        Cancha cancha = new Cancha();
        cancha.setId(this.canchaId);

        bloque.setCancha(cancha);
        bloque.setFechaInicio(this.fechaInicio);
        bloque.setFechaFin(this.fechaFin);
        bloque.setMotivo(this.motivo);

        return bloque;
    }
}