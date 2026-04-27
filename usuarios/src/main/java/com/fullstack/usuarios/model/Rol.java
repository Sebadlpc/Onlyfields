
package com.fullstack.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Size(max = 255)
    @Column(length = 255)
    private String descripcion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rol_permiso",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private List<Permiso> permisos;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;
}
