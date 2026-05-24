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
    
    @NotBlank(message = "El email del destinatario es obligatorio")
    private String destinatarioEmail; 
    
    @NotBlank(message = "El tipo de notificación es obligatorio") 
    
    @NotBlank(message = "El canal de notificación es obligatorio") 
    private String canal;
    
    @NotBlank(message = "El asunto es obligatorio") 
    private String asunto;
    
    @NotBlank(message = "El cuerpo de la notificación no puede estar vacío")
    private String cuerpo;
    
    private String estado;
    private LocalDateTime fechaEnvio;
    private Integer intentos;
    
    @NotBlank(message = "La clave de idempotencia es obligatoria")
    private String idempotencyKey;
}