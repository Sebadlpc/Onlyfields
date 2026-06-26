package com.fullstack.configuracion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Representa un día específico que es feriado o está bloqueado para ciertas operaciones.
 * Esta entidad se mapea a la tabla "feriados_bloqueos".
 */
@Entity
@Table(name = "feriados_bloqueos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa un día feriado o de bloqueo en el sistema")
public class FeriadoBloqueo {

    /**
     * Identificador único del registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro de feriado/bloqueo", example = "1")
    private Long id;

    /**
     * La fecha específica del feriado o bloqueo.
     * Debe ser única para evitar registros duplicados en el mismo día.
     */
    @Column(nullable = false, unique = true)
    @Schema(description = "Fecha del feriado o bloqueo", example = "2024-12-25")
    private LocalDate fecha;

    /**
     * El motivo del feriado o bloqueo (ej. "Navidad", "Mantenimiento del sistema").
     */
    @Column(nullable = false)
    @Schema(description = "Motivo por el cual el día es feriado o está bloqueado", example = "Navidad")
    private String motivo;

    /**
     * Un indicador booleano para saber si este día bloquea la creación de nuevas reservas.
     * Si es 'true', el sistema de reservas no debería permitir crear reservas en esta fecha.
     */
    @Column(nullable = false)
    @Schema(description = "Indica si este feriado impide la creación de nuevas reservas", example = "true")
    private Boolean afectaReservas;
}
