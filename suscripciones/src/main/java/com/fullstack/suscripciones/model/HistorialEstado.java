package com.fullstack.suscripciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Registra un cambio de estado en una suscripción para fines de auditoría.
 * Cada vez que una suscripción cambia de estado (ej. de "ACTIVA" a "CONGELADA"),
 * se crea un registro en esta tabla.
 * Se mapea a la tabla "historial_estado".
 */
@Entity
@Table(name = "historial_estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstado {

    /**
     * Identificador único del registro de historial.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La suscripción a la que pertenece este registro de historial.
     * Se utiliza @JsonIgnore para evitar bucles infinitos al serializar la suscripción,
     * ya que la suscripción podría tener una lista de historiales.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    /**
     * El estado de la suscripción *antes* del cambio. Puede ser nulo para el estado inicial.
     */
    @Column(name = "estado_anterior")
    private String estadoAnterior;

    /**
     * El estado de la suscripción *después* del cambio.
     */
    @Column(name = "estado_nuevo", nullable = false)
    private String estadoNuevo;

    /**
     * La fecha y hora exactas en que se produjo el cambio.
     */
    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    /**
     * Una descripción legible del motivo del cambio (ej. "Congelamiento por vacaciones", "Vencimiento automático").
     */
    private String motivo;
}
