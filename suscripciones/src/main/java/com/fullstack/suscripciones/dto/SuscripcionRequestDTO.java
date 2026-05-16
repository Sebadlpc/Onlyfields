package com.fullstack.suscripciones.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuscripcionRequestDTO {
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;
}