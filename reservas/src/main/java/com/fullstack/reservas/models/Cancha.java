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
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
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

    private String estado; 
}