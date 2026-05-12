package com.fullstack.reservas.dto;

import com.fullstack.reservas.models.Cancha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class CanchaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El deporte es obligatorio")
    private String deporte;

    @Positive(message = "La capacidad debe ser mayor a 0")
    private Integer capacidad;

    @Positive(message = "La tarifa por hora debe ser mayor a 0")
    private BigDecimal tarifaHora;

    private String estado;

    // Mapeo DTO
    public Cancha toEntity() {
        Cancha cancha = new Cancha();
        cancha.setNombre(this.nombre);
        cancha.setDeporte(this.deporte);
        cancha.setCapacidad(this.capacidad);
        cancha.setTarifaHora(this.tarifaHora);
        cancha.setEstado(this.estado != null ? this.estado : "DISPONIBLE");
        return cancha;
    }
}