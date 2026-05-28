package com.fullstack.inventario.dto;

import jakarta.validation.constraints.*;

/**
 * DTO (Data Transfer Object) para las solicitudes de actualización de stock.
 * Se utiliza para registrar entradas y salidas de productos.
 * Se define como un 'record' de Java para mayor concisión e inmutabilidad.
 *
 * @param tipo       Tipo de movimiento. Debe ser "ENTRADA" o "SALIDA" (insensible a mayúsculas/minúsculas en la lógica del servicio).
 * @param cantidad   La cantidad de unidades a mover. Debe ser al menos 1.
 * @param referencia Una descripción o código que justifique el movimiento (ej. ID de venta, orden de compra, ajuste).
 */
public record MovimientoStockDTO(
    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA o SALIDA)")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "El tipo debe ser ENTRADA o SALIDA")
    String tipo,

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad del movimiento debe ser un número positivo")
    Integer cantidad,

    @NotBlank(message = "Debe incluir una referencia (ej. ID de Venta POS o 'Ajuste Manual')")
    String referencia
) {
}
