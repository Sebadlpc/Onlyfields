package com.fullstack.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStockDTO {

    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA o SALIDA)")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "El tipo debe ser ENTRADA o SALIDA")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad del movimiento debe ser un número positivo")
    private Integer cantidad;

    @NotBlank(message = "Debe incluir una referencia (ej. ID de Venta POS o 'Ajuste Manual')")
    private String referencia;
}
