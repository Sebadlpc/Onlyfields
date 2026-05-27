package com.fullstack.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MovimientoStockDTO {

    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA o SALIDA)")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$", message = "El tipo debe ser ENTRADA o SALIDA")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad del movimiento debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "Debe incluir una referencia (ej. ID de Venta POS o 'Ajuste Manual')")
    private String referencia;
}