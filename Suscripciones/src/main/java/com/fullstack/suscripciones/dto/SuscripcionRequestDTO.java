package com.fullstack.suscripciones.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO para la solicitud de creación de una nueva suscripción.
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param clienteId   El ID del cliente que adquiere la suscripción.
 * @param planId      El ID del plan seleccionado.
 * @param fechaInicio La fecha en que la suscripción debe comenzar. No puede ser en el pasado.
 */
public record SuscripcionRequestDTO(
    @NotNull(message = "El ID del cliente es obligatorio")
    Long clienteId,

    @NotNull(message = "El ID del plan es obligatorio")
    Long planId,

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    LocalDate fechaInicio
) {
}
