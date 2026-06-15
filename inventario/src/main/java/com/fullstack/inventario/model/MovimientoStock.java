package com.fullstack.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa un movimiento de stock para un producto, ya sea una entrada o una salida.
 * Cada vez que se actualiza el stock de un producto, se debe crear un registro de este tipo
 * para mantener un historial de auditoría.
 * Esta entidad se mapea a la tabla "movimientos_stock".
 */
@Entity
@Table(name = "movimientos_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que registra un movimiento de stock (entrada o salida) de un producto")
public class MovimientoStock {

    /**
     * Identificador único del movimiento de stock.
     * Se genera automáticamente por la base de datos (autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del movimiento", example = "50")
    private Long id;

    /**
     * El producto al que está asociado este movimiento.
     * Es una relación muchos a uno con la entidad Producto.
     * La carga es perezosa (LAZY) para optimizar el rendimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Tipo de movimiento. Debería ser "ENTRADA" o "SALIDA".
     */
    @Column(nullable = false)
    @Schema(description = "Tipo de movimiento (ENTRADA o SALIDA)", example = "ENTRADA")
    private String tipo;

    /**
     * La cantidad de unidades que se movieron en esta transacción.
     * Siempre es un número positivo.
     */
    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades movidas", example = "50")
    private Integer cantidad;

    /**
     * La fecha y hora exactas en que se registró el movimiento.
     * Se establece automáticamente al crear el movimiento.
     */
    @Column(nullable = false)
    @Schema(description = "Fecha y hora del movimiento", example = "2024-05-20T14:00:00")
    private LocalDateTime fechaHora;

    /**
     * Una referencia o descripción del motivo del movimiento.
     * Por ejemplo, puede ser el ID de una venta, el número de una orden de compra,
     * o una nota como "Ajuste por inventario físico".
     */
    @Column
    @Schema(description = "Referencia o motivo del movimiento", example = "Compra a proveedor XYZ")
    private String referencia;
}
