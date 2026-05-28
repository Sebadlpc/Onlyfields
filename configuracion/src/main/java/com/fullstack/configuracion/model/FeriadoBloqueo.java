package com.fullstack.configuracion.model;

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
public class FeriadoBloqueo {

    /**
     * Identificador único del registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La fecha específica del feriado o bloqueo.
     * Debe ser única para evitar registros duplicados en el mismo día.
     */
    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    /**
     * El motivo del feriado o bloqueo (ej. "Navidad", "Mantenimiento del sistema").
     */
    @Column(nullable = false)
    private String motivo;

    /**
     * Un indicador booleano para saber si este día bloquea la creación de nuevas reservas.
     * Si es 'true', el sistema de reservas no debería permitir crear reservas en esta fecha.
     */
    @Column(nullable = false)
    private Boolean afectaReservas;
}
