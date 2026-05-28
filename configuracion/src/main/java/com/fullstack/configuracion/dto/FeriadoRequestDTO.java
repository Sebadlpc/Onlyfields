package com.fullstack.configuracion.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para la solicitud de registro de un nuevo feriado o día de bloqueo.
 * Se define como un 'record' de Java para concisión e inmutabilidad.
 *
 * @param fecha          La fecha del feriado. No puede ser una fecha pasada.
 * @param motivo         La razón del feriado o bloqueo. No puede estar vacío.
 * @param afectaReservas Un booleano que indica si este día inhabilita la creación de reservas.
 */
public record FeriadoRequestDTO(
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "No se pueden registrar bloqueos en fechas pasadas")
    LocalDate fecha,

    @NotBlank(message = "Debe indicar un motivo para el bloqueo")
    String motivo,

    @NotNull(message = "Debe indicar si afecta a las reservas")
    Boolean afectaReservas
) {
}
