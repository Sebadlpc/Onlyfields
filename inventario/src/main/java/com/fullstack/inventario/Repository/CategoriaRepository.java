package com.fullstack.inventario.Repository;


import com.fullstack.inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Métodos estándar de JpaRepository (save, findAll, delete, etc.)
}
