package com.fullstack.pos.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "items_transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItemTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;

    @Column(name = "producto_id")
    private Long productoId;

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Positive(message = "EL precio unitario debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    public Double getSubTotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            return this.cantidad * this.precioUnitario;
        }
        return 0.0;
    }
}
