package com.fullstack.suscripciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de congelamiento de una suscripción.
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param dias   El número de días que se desea congelar la suscripción. Debe ser al menos 1.
 * @param motivo La razón del congelamiento (ej. "Vacaciones", "Lesión").
 */
public record CongelarRequestDTO(
    @NotNull(message = "El número de días es obligatorio.")
    @Min(value = 1, message = "Debe congelar por lo menos 1 día.")
    Integer dias,

    @NotBlank(message = "Debe proporcionar un motivo para el congelamiento.")
    String motivo
) {
}
