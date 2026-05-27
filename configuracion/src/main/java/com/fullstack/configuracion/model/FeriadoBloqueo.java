package com.fullstack.configuracion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "feriados_bloqueos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeriadoBloqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private Boolean afectaReservas;
}