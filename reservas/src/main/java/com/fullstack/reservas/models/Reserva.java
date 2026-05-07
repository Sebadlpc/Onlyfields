package com.fullstack.reservas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "reserva")
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mi_secuencia")
    @SequenceGenerator(name = "mi_secuencia", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "cancha_id")
    private Long canchaId;

    @NotNull
    @Column(name = "cliente_id")
    private Long clienteId;

    @NotNull
    @Future(message = "La reserva debe ser en una fecha futura")
    private LocalDateTime fechaInicio;

    @NotNull
    private LocalDateTime fechaFin;

    @Column(length = 30)
    private String estado; 

    @Column(name = "total_cobrado", precision = 12, scale = 2)
    private BigDecimal totalCobrado;
}
