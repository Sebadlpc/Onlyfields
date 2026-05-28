package com.fullstack.configuracion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Representa una configuración global del sistema, definida por un par clave-valor.
 * Ejemplos: "TASA_IVA" -> "0.21", "HORARIO_APERTURA" -> "09:00".
 * Esta entidad se mapea a la tabla "configuracion_global".
 */
@Entity
@Table(name = "configuracion_global")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionGlobal {

    /**
     * Identificador único de la configuración.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La clave única que identifica la configuración (ej. "HORARIO_APERTURA").
     * Es inmutable y se usa para buscar el valor.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private String clave;

    /**
     * El valor asociado a la clave. Este campo es el que se modifica.
     */
    @Column(nullable = false)
    private String valor;

    /**
     * Una descripción legible por humanos sobre lo que hace esta configuración.
     */
    private String descripcion;

    /**
     * La fecha y hora de la última modificación de este registro.
     * Se actualiza automáticamente desde la capa de servicio.
     */
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    /**
     * El ID del usuario que realizó la última modificación.
     * Útil para auditoría.
     */
    @Column(nullable = false)
    private Long usuarioId;
}
