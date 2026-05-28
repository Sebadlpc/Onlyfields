package com.fullstack.suscripciones.model;

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
public class Suscripcion {

    /**
     * Identificador único de la suscripción.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del cliente (usuario) al que pertenece la suscripción.
     * Este ID se corresponde con un usuario en el microservicio de Usuarios.
     */
    @Column(name = "cliente_id", nullable = false)
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
    private LocalDate fechaInicio;

    /**
     * Fecha en la que la suscripción expira.
     * Esta fecha puede extenderse si la suscripción se congela.
     */
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    /**
     * Estado actual de la suscripción (ej. "ACTIVA", "CONGELADA", "VENCIDA", "CANCELADA").
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Contador de días que la suscripción ha estado congelada.
     * Puede usarse para limitar el número total de días de congelamiento permitidos.
     */
    @Column(name = "dias_congelados", nullable = false)
    private Integer diasCongelados;
}
