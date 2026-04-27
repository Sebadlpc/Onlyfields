// Ruta: usuarios/src/main/java/com/fullstack/usuarios/model/RolPermiso.java
package com.fullstack.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol_permiso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RolPermisoId.class) // Composite key
public class RolPermiso {
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
}
