package com.fullstack.notificaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_seq")
    @SequenceGenerator(name = "notif_seq", sequenceName = "NOTIFICACION_SEQ", allocationSize = 1)
    private Long id;

    
    @NotNull(message = "El ID del destinatario no puede ser nulo")
    @Column(name = "destinatario_id")
    private Long destinatarioId;

    // Validación para el email del destinatario
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe tener un formato de correo válido")
    @Column(name = "destinatario_email")
    private String destinatarioEmail;
    
    @NotNull(message = "El tipo de notificación es obligatorio")
    private String tipo;

    @NotNull(message = "El canal es obligatorio")
    @Pattern(regexp = "EMAIL|SMS|PUSH", message = "El canal debe ser EMAIL, SMS o PUSH")
    private String canal;

    @NotBlank(message = "El asunto no puede estar vacío")
    @Size(max = 200, message = "El asunto no puede tener más de 200 caracteres")
    private String asunto;

    @NotBlank(message = "El cuerpo del mensaje no puede estar vacío")
    @Lob // @Lob por si el cuerpo del correo (HTML) es muy largo
    private String cuerpo;

    @Column(length = 20)
    private String estado = "PENDIENTE"; // Estado inicial por defecto

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    private Integer intentos = 0; // Inicia en 0 intentos

    // Llave única para evitar envíos duplicados (Idempotencia)
    @NotBlank(message = "La llave de idempotencia es obligatoria")
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;
}