package com.fullstack.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "producto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotNull
    @Column(name = "Precio_venta", nullable = false)
    private Double precioVenta;

    @NotNull
    @Column(name = "Stock_actual", nullable = false)
    private Integer stockActual;

    @NotNull
    @Column(name = "Stock_minimo", nullable = false)
    private Integer stockMinimo;

    @NotBlank
    @Column(name = "Codigo_barras", nullable = false)
    private String codigoBarras;

}
