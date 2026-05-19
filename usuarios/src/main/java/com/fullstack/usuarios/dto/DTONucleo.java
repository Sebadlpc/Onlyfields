package com.fullstack.usuarios.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class DTONucleo {

    @Data
    public static class AuthLoginDTO {
        @NotBlank @Email private String correoElectronico;
        @NotBlank private String password;
    }

    @Data
    @Builder
    public static class RolDTO {
        private Long id;
        @NotBlank private String nombre;
        private String descripcion;
    }

    @Data
    public static class UsuarioRegistroDTO {
        @NotBlank private String nombre;
        @NotBlank @Email private String correoElectronico;
        @NotBlank @Size(min = 8) private String password;
        @NotNull private Long rolId;
    }

    @Data
    @Builder
    public static class UsuarioRespuestaDTO {
        private Long id;
        private String nombre;
        private String correoElectronico;
        private String estado;
        private LocalDateTime fechaCreacion;
        private String rolNombre;
    }
}