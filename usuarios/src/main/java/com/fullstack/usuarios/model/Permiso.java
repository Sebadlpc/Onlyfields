// Ruta: usuarios/src/main/java/com/fullstack/usuarios/model/Permiso.java
package com.fullstack.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permiso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso", nullable = false, updatable = false)
    private Long id;
    
    @NotBlank(message = "El módulo es obligatorio")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String modulo;
    
    @NotBlank(message = "La acción es obligatoria")
    @Size(min = 2, max = 20)
    @Column(nullable = false)
    private String accion;
    
    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Rol> roles = new ArrayList<>();
}
