package com.fullstack.configuracion.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_global")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UK (Unique Key): Garantiza que no haya dos configuraciones con la misma clave
    @Column(nullable = false, unique = true)
    private String clave;

    @Column(nullable = false)
    private String valor;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    // Referencia al Microservicio 1 (MS1) - No es una relación de BD local
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
}