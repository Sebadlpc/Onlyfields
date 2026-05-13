package com.fullstack.notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDTO {
    private Long id;
    
    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long destinatarioId;
    
    private String tipo;
    private String canal;
    private String asunto;
    
    @NotBlank(message = "El cuerpo de la notificación no puede estar vacío")
    private String cuerpo;
    
    private String estado;
    private LocalDateTime fechaEnvio;
    private Integer intentos;
    private String idempotencyKey;
}