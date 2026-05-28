package com.fullstack.suscripciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongelarRequestDTO {

    @NotNull(message = "El número de días es obligatorio.")
    @Min(value = 1, message = "Debe congelar por lo menos 1 día.")
    private Integer dias;

    @NotBlank(message = "Debe proporcionar un motivo para el congelamiento.")
    private String motivo;
}
