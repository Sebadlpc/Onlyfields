package com.fullstack.reservas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "cancha")
@Data
public class Cancha {
   @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mi_secuencia")
    @SequenceGenerator(name = "mi_secuencia", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String deporte;

    @Positive
    private Integer capacidad;

    @Positive
    @Column(precision = 12, scale = 2)
    private BigDecimal tarifaHora;

    private String estado; // Ejemplo: 'DISPONIBLE', 'MANTENIMIENTO'
}