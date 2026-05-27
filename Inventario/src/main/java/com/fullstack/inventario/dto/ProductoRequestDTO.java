package com.fullstack.inventario.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductoRequestDTO {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    @NotNull(message = "Debe asignar una categoría al producto")
    private Long categoriaId;

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor a cero")
    private BigDecimal precioVenta;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    private String codigoBarras;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}