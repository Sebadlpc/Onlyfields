package com.fullstack.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRespuestaDTO {

    private Long id;
    private String nombre;
    private String correoElectronico;
    private String estado;
    private LocalDateTime fechaCreacion;
    private String rolNombre;
}
