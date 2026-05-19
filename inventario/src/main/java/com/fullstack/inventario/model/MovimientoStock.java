package com.fullstack.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_stock")
@Data
public class MovimientoStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(ENTRADA|SALIDA|AJUSTE|MERMA)$", message = "Debe ser ENTRADA, SALIDA, AJUSTE o MERMA")
    private String tipo;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private LocalDateTime fechaHora;

    @NotBlank(message = "La referencia es obligatoria")
    private String referencia;
}