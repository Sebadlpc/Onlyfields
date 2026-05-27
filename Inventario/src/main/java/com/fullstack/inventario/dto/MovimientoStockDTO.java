package com.fullstack.inventario.dto;

import jakarta.validation.constraints.*;

public class MovimientoStockDTO {

    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA o SALIDA)")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$", message = "El tipo debe ser ENTRADA o SALIDA")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad del movimiento debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "Debe incluir una referencia (ej. ID de Venta POS o 'Ajuste Manual')")
    private String referencia;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}