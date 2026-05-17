package com.fullstack.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MovimientoRequestDTO {
    @NotBlank(message = "El tipo debe ser ENTRADA o SALIDA")
    private String tipo;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "La referencia es obligatoria")
    private String referencia;
}