package com.fullstack.configuracion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfiguracionRequestDTO {

    @NotBlank(message = "La clave no puede estar vacía")
    private String clave;

    @NotBlank(message = "El valor no puede estar vacío")
    private String valor;

    private String descripcion;

    @NotNull(message = "El ID del usuario es obligatorio para auditoría")
    private Long usuarioId;
}