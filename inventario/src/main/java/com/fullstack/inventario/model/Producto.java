package com.fullstack.inventario.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Representa un producto en el inventario.
 * Esta entidad se mapea a la tabla "productos" en la base de datos.
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    /**
     * Identificador único del producto.
     * Se genera automáticamente por la base de datos (autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto. Es un campo obligatorio.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Categoría a la que pertenece el producto.
     * Es una relación muchos a uno con la entidad Categoria.
     * La carga es perezosa (LAZY) para optimizar el rendimiento, cargando la categoría solo cuando se accede a ella.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Precio de venta al público del producto. Es un campo obligatorio.
     */
    @Column(nullable = false)
    private BigDecimal precioVenta;

    /**
     * Cantidad actual de unidades disponibles en stock. Es un campo obligatorio.
     */
    @Column(nullable = false)
    private Integer stockActual;

    /**
     * Nivel mínimo de stock. Cuando el stockActual cae por debajo de este número,
     * se puede generar una alerta. Es un campo obligatorio.
     */
    @Column(nullable = false)
    private Integer stockMinimo;

    /**
     * Código de barras único para identificar el producto.
     * La restricción 'unique' asegura que no haya dos productos con el mismo código.
     */
    @Column(unique = true)
    private String codigoBarras;
}
