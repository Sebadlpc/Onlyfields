package com.fullstack.suscripciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Representa un tipo de plan de suscripción que un cliente puede adquirir (ej. "Plan Mensual", "Plan Anual").
 * Se mapea a la tabla "plan".
 */
@Entity
@Table(name = "plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    /**
     * Identificador único del plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del plan (ej. "Suscripción Premium").
     */
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Duración del plan en días (ej. 30 para un plan mensual).
     */
    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    /**
     * El costo del plan.
     */
    @Column(nullable = false)
    private BigDecimal precio;

    /**
     * Una descripción de los beneficios incluidos en el plan.
     * Puede ser un texto simple o un JSON.
     */
    @Lob // Large Object, permite almacenar textos largos.
    private String beneficios;
}
