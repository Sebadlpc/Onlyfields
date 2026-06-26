package com.fullstack.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa una categoría de productos")
public class Categoria {

    /**
     * Identificador único de la categoría.
     * Se genera automáticamente por la base de datos (autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la categoría", example = "5")
    private Long id;

    /**
     * Nombre de la categoría.
     * Debe ser único y no puede ser nulo.
     */
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre de la categoría", example = "Bebidas Isotónicas")
    private String nombre;
}
