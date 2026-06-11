package com.fullstack.pos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_transaccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detalle de los productos o servicios de una transacción")
public class ItemTransaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del ítem", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id", nullable = false)
    @Schema(hidden = true)
    private Transaccion transaccion;

    @Column(nullable = false)
    @Schema(description = "ID del producto o plan comprado", example = "201")
    private Long productoId;

    @Schema(description = "Descripción o nombre del ítem", example = "Bebida Energética 500ml")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades", example = "2")
    private Integer cantidad;

    @Column(nullable = false)
    @Schema(description = "Precio unitario del producto", example = "1500.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Subtotal calculado del ítem", example = "3000.00", accessMode = Schema.AccessMode.READ_ONLY)
    public BigDecimal getSubTotal() {
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }
}
