package com.fullstack.notificaciones.model;

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
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destinatario_id")
    private Long destinatarioId;

    @Column(name = "destinatario_email")
    private String destinatarioEmail;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "canal")
    private String canal;

    @Column(name = "asunto")
    private String asunto;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    @Builder.Default
    @Column(name = "estado")
    private String estado = "PENDIENTE";

    @Builder.Default
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Builder.Default
    @Column(name = "intentos")
    private Integer intentos = 0;

    @Column(name = "idempotency_key", unique = true)
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