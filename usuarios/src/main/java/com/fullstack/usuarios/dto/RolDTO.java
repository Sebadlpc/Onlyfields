package com.fullstack.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolDTO {

    private Long id;

    @NotBlank(message = "El nombre del rol no puede estar vacío.")
    private String nombre;

    private String descripcion;
}
