package com.fullstack.notificaciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una notificación a un usuario")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;

    @Column(name = "destinatario_id")
    @Schema(description = "ID del usuario destinatario", example = "123")
    private Long destinatarioId;

    @Column(name = "destinatario_email")
    @Schema(description = "Email del destinatario", example = "usuario@example.com")
    private String destinatarioEmail;

    @Column(name = "tipo")
    @Schema(description = "Tipo de notificación (ej. BIENVENIDA, RECORDATORIO, COMPROBANTE)", example = "COMPROBANTE")
    private String tipo;

    @Column(name = "canal")
    @Schema(description = "Canal de envío de la notificación (EMAIL, SMS, PUSH)", example = "EMAIL")
    private String canal;

    @Column(name = "asunto")
    @Schema(description = "Asunto de la notificación", example = "Confirmación de Reserva")
    private String asunto;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Cuerpo del mensaje de la notificación", example = "Tu reserva ha sido confirmada.")
    private String cuerpo;

    @Builder.Default
    @Column(name = "estado")
    @Schema(description = "Estado actual de la notificación (PENDIENTE, ENVIADO, FALLIDO)", example = "PENDIENTE")
    private String estado = "PENDIENTE";

    @Builder.Default
    @Column(name = "fecha_envio")
    @Schema(description = "Fecha y hora del último intento de envío")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Builder.Default
    @Column(name = "intentos")
    @Schema(description = "Número de intentos de envío", example = "0")
    private Integer intentos = 0;

    @Column(name = "idempotency_key", unique = true)
    @Schema(description = "Clave de idempotencia para evitar duplicados", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private String idempotencyKey;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
        if (this.intentos == null) {
            this.intentos = 0;
        }
        if (this.fechaEnvio == null) {
            this.fechaEnvio = LocalDateTime.now();
        }
    }
}