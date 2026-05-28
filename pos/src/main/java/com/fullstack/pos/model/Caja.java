package com.fullstack.pos.model;

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
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fechaApertura;

    private LocalDateTime fechaCierre;

    @Column(nullable = false)
    private BigDecimal montoInicial;

    @Builder.Default
    private BigDecimal totalEfectivo = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalTarjeta = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoCaja estado = EstadoCaja.ABIERTA;

    @PrePersist
    protected void onCreate() {
        fechaApertura = LocalDateTime.now();
    }
}
