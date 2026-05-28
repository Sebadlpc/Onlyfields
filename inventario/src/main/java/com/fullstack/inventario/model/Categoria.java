package com.fullstack.inventario.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa una categoría de productos (ej. "Bebidas", "Snacks", etc.).
 * Esta entidad se mapea a la tabla "categorias" en la base de datos.
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    /**
     * Identificador único de la categoría.
     * Se genera automáticamente por la base de datos (autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la categoría.
     * Debe ser único y no puede ser nulo.
     */
    @Column(nullable = false, unique = true)
    private String nombre;
}
