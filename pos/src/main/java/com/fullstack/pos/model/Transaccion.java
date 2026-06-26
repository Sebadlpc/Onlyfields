package com.fullstack.pos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "transaccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad matriz de transacciones financieras")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la transacción", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "ID de la caja que procesó el pago", example = "1")
    private Long cajaId;

    @Schema(description = "ID del cliente asociado", example = "105")
    private Long clienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo de operación realizada", example = "VENTA_PRODUCTO")
    private TipoTransaccion tipo;

    @Column(nullable = false)
    @Schema(description = "Monto total de la transacción", example = "3000.00")
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Método de pago seleccionado", example = "EFECTIVO")
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Estado del registro transaccional", example = "COMPLETADA")
    private EstadoTransaccion estado = EstadoTransaccion.COMPLETADA;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora del movimiento", example = "2026-06-11T13:15:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaHora;

    @OneToMany(mappedBy = "transaccion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Lista con el desglose de ítems comprados")
    private List<ItemTransaccion> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }
}
