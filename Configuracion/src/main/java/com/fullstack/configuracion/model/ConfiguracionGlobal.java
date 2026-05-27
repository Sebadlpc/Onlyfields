package com.fullstack.configuracion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_global")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracionGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String clave;

    @Column(nullable = false)
    private String valor;

    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private Long usuarioId;
}