package com.fullstack.suscripciones.dto.external;

import lombok.Data;

@Data
public class UsuarioExternoDTO {
    private Long id;
    private String nombre;
    private String correoElectronico;
    private String estado;
}