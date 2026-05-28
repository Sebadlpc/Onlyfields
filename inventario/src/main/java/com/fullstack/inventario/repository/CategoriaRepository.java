package com.fullstack.inventario.repository;

import com.fullstack.inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Categoria}.
 * Proporciona todos los métodos CRUD básicos para la gestión de categorías de productos.
 * No se necesitan consultas personalizadas adicionales por el momento.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
