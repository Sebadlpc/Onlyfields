package com.fullstack.pos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad de control de flujos y estado de caja")
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de caja", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "ID del usuario o cajero", example = "5")
    private Long usuarioId;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora de apertura", example = "2026-06-11T09:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaApertura;

    @Schema(description = "Fecha y hora de cierre", example = "2026-06-11T21:00:00")
    private LocalDateTime fechaCierre;

    @Column(nullable = false)
    @Schema(description = "Monto inicial de apertura", example = "50000.00")
    private BigDecimal montoInicial;

    @Builder.Default
    @Schema(description = "Total acumulado en efectivo", example = "125000.00")
    private BigDecimal totalEfectivo = BigDecimal.ZERO;

    @Builder.Default
    @Schema(description = "Total acumulado con tarjetas", example = "85000.00")
    private BigDecimal totalTarjeta = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Estado actual de la caja", example = "ABIERTA")
    private EstadoCaja estado = EstadoCaja.ABIERTA;

    @PrePersist
    protected void onCreate() {
        fechaApertura = LocalDateTime.now();
    }
}
