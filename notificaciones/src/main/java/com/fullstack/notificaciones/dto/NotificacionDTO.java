package com.fullstack.notificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Email(message = "Debe ser una dirección de email válida")
    private String destinatarioEmail; 
    
    @NotBlank(message = "El tipo de notificación es obligatorio") 
    private String tipo;
    
    @NotBlank(message = "El canal de notificación es obligatorio") 
    @Pattern(regexp = "EMAIL|SMS|PUSH", message = "El canal solo puede ser EMAIL, SMS o PUSH")
    private String canal;
    
    @NotBlank(message = "El asunto es obligatorio") 
    @Size(max = 200, message = "El asunto no puede superar los 200 caracteres")
    private String asunto;
    
    @NotBlank(message = "El cuerpo de la notificación no puede estar vacío")
    private String cuerpo;
    
    private String estado;
    private LocalDateTime fechaEnvio;
    private Integer intentos;
    
    @NotBlank(message = "La clave de idempotencia es obligatoria")
    private String idempotencyKey;
}