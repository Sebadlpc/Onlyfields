package com.fullstack.seguimiento.dto;

import com.fullstack.seguimiento.Model.ObjetivoFisico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicionCorporalDTO {
    private Long id;
    private Long fichaId;
    private LocalDate fecha;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a cero")
    private Double peso;

    @NotNull(message = "La altura es obligatoria")
    @Positive(message = "La altura debe ser mayor a cero")
    private Double altura;

    private Double imc;
    @Positive
    private Double cintura;
    @Positive
    private Double cadera;
    private ObjetivoFisico objetivo;
}
