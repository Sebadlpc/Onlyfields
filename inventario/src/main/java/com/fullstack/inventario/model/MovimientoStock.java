package com.fullstack.inventario.model;

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
public class MovimientoStock {

    /**
     * Identificador único del movimiento de stock.
     * Se genera automáticamente por la base de datos (autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String tipo;

    /**
     * La cantidad de unidades que se movieron en esta transacción.
     * Siempre es un número positivo.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * La fecha y hora exactas en que se registró el movimiento.
     * Se establece automáticamente al crear el movimiento.
     */
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Una referencia o descripción del motivo del movimiento.
     * Por ejemplo, puede ser el ID de una venta, el número de una orden de compra,
     * o una nota como "Ajuste por inventario físico".
     */
    @Column
    private String referencia;
}
