package com.fullstack.usuarios.repository;

import com.fullstack.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Usuario}.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * Utiliza @EntityGraph para cargar ansiosamente (eagerly) la colección de roles
     * en la misma consulta, evitando así el problema de N+1 selects.
     *
     * @param email El correo electrónico del usuario a buscar.
     */
    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con el correo electrónico proporcionado.
     * Este método es más eficiente que buscar el usuario completo si solo se necesita
     * comprobar la existencia.
     *
     * @param email El correo electrónico a verificar.
     */
    boolean existsByEmail(String email);
}
