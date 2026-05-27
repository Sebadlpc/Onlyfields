package com.fullstack.suscripciones.dto;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class CongelarRequestDTO {
    @Min(value = 1, message = "Debe congelar al menos 1 día")
    private Integer dias;
    @NotBlank(message = "Debe proveer un motivo de congelamiento")
    private String motivo;
}