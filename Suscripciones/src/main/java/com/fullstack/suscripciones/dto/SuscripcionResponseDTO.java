package com.fullstack.suscripciones.dto;

import java.time.LocalDate;

/**
 * DTO para devolver la información completa de una suscripción.
 * Se utiliza como respuesta en los endpoints para exponer los datos de la suscripción.
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param id             El ID único de la suscripción.
 * @param clienteId      El ID del cliente.
 * @param planId         El ID del plan asociado.
 * @param planNombre     El nombre del plan para conveniencia del frontend.
 * @param fechaInicio    La fecha de inicio de la suscripción.
 * @param fechaFin       La fecha de fin calculada de la suscripción.
 * @param estado         El estado actual de la suscripción (ej. "ACTIVA").
 * @param diasCongelados El número de días que la suscripción ha estado congelada.
 */
public record SuscripcionResponseDTO(
    Long id,
    Long clienteId,
    Long planId,
    String planNombre,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    String estado,
    Integer diasCongelados
) {
}
