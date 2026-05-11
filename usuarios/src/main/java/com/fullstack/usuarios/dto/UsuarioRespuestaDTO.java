package com.fullstack.usuarios.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioRespuestaDTO {
    
    private Long id;
    private String nombre;
    private String correoElectronico;
    private String estado;
    private LocalDateTime fechaCreacion;
    
    private String rolNombre; 
}