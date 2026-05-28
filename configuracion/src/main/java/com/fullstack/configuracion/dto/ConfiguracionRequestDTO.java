package com.fullstack.configuracion.dto;

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
public class ConfiguracionRequestDTO {

    @NotBlank(message = "El valor no puede estar vacío")
    private String valor;

    private String descripcion;

    @NotNull(message = "El ID del usuario es obligatorio para auditoría")
    private Long usuarioId;
}
