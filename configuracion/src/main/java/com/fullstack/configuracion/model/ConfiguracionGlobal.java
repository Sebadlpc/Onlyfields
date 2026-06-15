package com.fullstack.configuracion.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa una configuración global del sistema (clave-valor)")
public class ConfiguracionGlobal {

    /**
     * Identificador único de la configuración.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la configuración", example = "1")
    private Long id;

    /**
     * La clave única que identifica la configuración (ej. "HORARIO_APERTURA").
     * Es inmutable y se usa para buscar el valor.
     */
    @Column(nullable = false, unique = true, updatable = false)
    @Schema(description = "Clave única de la configuración", example = "TASA_IVA")
    private String clave;

    /**
     * El valor asociado a la clave. Este campo es el que se modifica.
     */
    @Column(nullable = false)
    @Schema(description = "Valor asociado a la clave", example = "19")
    private String valor;

    /**
     * Una descripción legible por humanos sobre lo que hace esta configuración.
     */
    @Schema(description = "Descripción de la configuración", example = "Impuesto al Valor Agregado")
    private String descripcion;

    /**
     * La fecha y hora de la última modificación de este registro.
     * Se actualiza automáticamente desde la capa de servicio.
     */
    @Column(nullable = false)
    @Schema(description = "Fecha de la última modificación", example = "2024-05-20T10:00:00")
    private LocalDateTime fechaModificacion;

    /**
     * El ID del usuario que realizó la última modificación.
     * Útil para auditoría.
     */
    @Column(nullable = false)
    @Schema(description = "ID del usuario que modificó la configuración", example = "1")
    private Long usuarioId;
}
