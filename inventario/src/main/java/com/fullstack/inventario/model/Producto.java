package com.fullstack.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "producto", uniqueConstraints = {@UniqueConstraint(columnNames = "codigo_barras")})
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Positive(message = "El precio de venta debe ser mayor a 0")
    private BigDecimal precioVenta;

    @PositiveOrZero(message = "El stock actual no puede ser negativo")
    private Integer stockActual;

    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @NotBlank(message = "El código de barras es obligatorio")
    @Pattern(regexp = "^\\d{8,13}$", message = "El código de barras debe ser numérico y tener entre 8 y 13 dígitos") //
    @Column(name = "codigo_barras")
    private String codigoBarras;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private String estado = "ACTIVO";

    @Version
    private Long version;
}