package com.fullstack.suscripciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Representa la suscripción de un cliente a un plan específico.
 * Esta es la entidad principal del microservicio.
 * Se mapea a la tabla "suscripcion".
 */
@Entity
@Table(name = "suscripcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa la suscripción de un cliente a un plan")
public class Suscripcion {

    /**
     * Identificador único de la suscripción.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la suscripción", example = "101")
    private Long id;

    /**
     * ID del cliente (usuario) al que pertenece la suscripción.
     * Este ID se corresponde con un usuario en el microservicio de Usuarios.
     */
    @Column(name = "cliente_id", nullable = false)
    @Schema(description = "ID del cliente asociado a la suscripción", example = "12345")
    private Long clienteId;

    /**
     * El plan al que está suscrito el cliente.
     * Es una relación muchos a uno con la entidad Plan.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    /**
     * Fecha en la que la suscripción se vuelve activa.
     */
    @Column(name = "fecha_inicio", nullable = false)
    @Schema(description = "Fecha de inicio de la suscripción", example = "2024-01-01")
    private LocalDate fechaInicio;

    /**
     * Fecha en la que la suscripción expira.
     * Esta fecha puede extenderse si la suscripción se congela.
     */
    @Column(name = "fecha_fin", nullable = false)
    @Schema(description = "Fecha de finalización de la suscripción", example = "2024-12-31")
    private LocalDate fechaFin;

    /**
     * Estado actual de la suscripción (ej. "ACTIVA", "CONGELADA", "VENCIDA", "CANCELADA").
     */
    @Column(nullable = false)
    @Schema(description = "Estado actual de la suscripción", example = "ACTIVA")
    private String estado;

    /**
     * Contador de días que la suscripción ha estado congelada.
     * Puede usarse para limitar el número total de días de congelamiento permitidos.
     */
    @Column(name = "dias_congelados", nullable = true)
    @Schema(description = "Número de días que la suscripción ha estado congelada", example = "0")
    private Integer diasCongelados;
}
