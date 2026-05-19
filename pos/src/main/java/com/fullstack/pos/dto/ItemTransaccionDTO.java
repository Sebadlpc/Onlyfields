package com.fullstack.pos.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemTransaccionDTO {
    private Long id;
    private Long transaccionId; // Solo el ID, no el objeto completo
    private Long productoId;
    private String descripcion;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subTotal;
}
