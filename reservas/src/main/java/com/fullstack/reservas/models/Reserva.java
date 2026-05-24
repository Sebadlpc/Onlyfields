package com.fullstack.reservas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "reserva")
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cancha_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cancha cancha;

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

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}