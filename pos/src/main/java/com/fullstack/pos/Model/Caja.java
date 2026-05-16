package com.fullstack.pos.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cajas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @PositiveOrZero
    @Column(name = "monto_inicial", nullable = false)
    private Double montoInicial;

    @PositiveOrZero
    @Column(name = "total_efectivo")
    private Float totalEfectivo = 0f;

    @PositiveOrZero
    @Column(name = "total_tarjeta")
    private Float totalTarjeta = 0f;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCaja estado;

    @PrePersist
    public void prePersist() {
        this.fechaApertura = LocalDateTime.now();
        this.estado = EstadoCaja.ABIERTA;
    }
}
