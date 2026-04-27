
package com.fullstack.usuarios.model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
public class RolPermisoId implements Serializable {
    
    private Long rolId;
    private Long permisoId;
}
