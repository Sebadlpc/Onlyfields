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

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "canal")
    private String canal;

    @Column(name = "asunto")
    private String asunto;


    @NotBlank
    @Lob
    @Column(name = "cuerpo", columnDefinition = "CLOB")
    private String cuerpo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "intentos")
    private Integer intentos;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;
}