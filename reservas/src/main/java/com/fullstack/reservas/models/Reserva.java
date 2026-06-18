package com.fullstack.reservas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@Data
@Schema(description = "Entidad que representa una reserva de una cancha por un cliente")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reserva", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cancha_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @Schema(description = "Cancha asociada a la reserva")
    private Cancha cancha;

    @NotNull
    @Column(name = "cliente_id")
    @Schema(description = "ID del cliente que realiza la reserva", example = "123")
    private Long clienteId;

    @NotNull
    @Future(message = "La reserva debe ser en una fecha futura")
    @Schema(description = "Fecha y hora de inicio de la reserva")
    private LocalDateTime fechaInicio;

    @NotNull
    @Schema(description = "Fecha y hora de fin de la reserva")
    private LocalDateTime fechaFin;

    @Column(length = 30)
    @Schema(description = "Estado actual de la reserva (PENDIENTE_PAGO, CONFIRMADA, CANCELADA)", example = "CONFIRMADA")
    private String estado;

    @Column(name = "total_cobrado", precision = 12, scale = 2)
    @Schema(description = "Monto total cobrado por la reserva", example = "50.00")
    private BigDecimal totalCobrado;

    @Column(name = "fecha_creacion", updatable = false)
    @Schema(description = "Fecha y hora en que se creó la reserva")
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}