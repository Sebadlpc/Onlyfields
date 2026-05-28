package com.fullstack.configuracion.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeriadoRequestDTO {

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "No se pueden registrar bloqueos en fechas pasadas")
    private LocalDate fecha;

    @NotBlank(message = "Debe indicar un motivo para el bloqueo")
    private String motivo;

    @NotNull(message = "Debe indicar si afecta a las reservas")
    private Boolean afectaReservas;
}
