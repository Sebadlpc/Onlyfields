package com.fullstack.suscripciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa un plan de suscripción ofrecido")
public class Plan {

    /**
     * Identificador único del plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del plan", example = "1")
    private Long id;

    /**
     * Nombre del plan (ej. "Suscripción Premium").
     */
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre comercial del plan", example = "Suscripción Premium")
    private String nombre;

    /**
     * Duración del plan en días (ej. 30 para un plan mensual).
     */
    @Column(name = "duracion_dias", nullable = false)
    @Schema(description = "Duración del plan expresada en días", example = "30")
    private Integer duracionDias;

    /**
     * El costo del plan.
     */
    @Column(nullable = false)
    @Schema(description = "Costo asociado al plan", example = "14.99")
    private BigDecimal precio;

    /**
     * Una descripción de los beneficios incluidos en el plan.
     * Puede ser un texto simple o un JSON.
     */
    @Lob // Large Object, permite almacenar textos largos.
    @Column(columnDefinition = "text")
    @Schema(description = "Descripción detallada de los beneficios del plan", example = "Acceso completo a todas las instalaciones en horario extendido")
    private String beneficios;
}
