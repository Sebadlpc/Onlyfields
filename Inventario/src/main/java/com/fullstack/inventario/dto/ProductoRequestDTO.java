package com.fullstack.inventario.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para las solicitudes de creación y actualización de productos.
 * Utiliza anotaciones de validación para asegurar la integridad de los datos de entrada.
 * Se define como un 'record' de Java para mayor concisión e inmutabilidad.
 *
 * @param nombre        Nombre del producto. No puede estar vacío.
 * @param categoriaId   ID de la categoría a la que pertenece el producto. No puede ser nulo.
 * @param precioVenta   Precio de venta del producto. Debe ser un número positivo.
 * @param stockActual   Stock inicial o actual del producto. No puede ser negativo.
 * @param stockMinimo   Nivel de stock mínimo para alertas. No puede ser negativo.
 * @param codigoBarras  Código de barras único del producto (opcional).
 */
public record ProductoRequestDTO(
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    String nombre,

    @NotNull(message = "Debe asignar una categoría al producto")
    Long categoriaId,

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor a cero")
    BigDecimal precioVenta,

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    Integer stockActual,

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    Integer stockMinimo,

    String codigoBarras
) {
}
