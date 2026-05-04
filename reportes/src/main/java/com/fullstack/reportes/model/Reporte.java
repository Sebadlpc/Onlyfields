package com.fullstack.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_generado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    /**
     * Referencia al ID del usuario en el microservicio de Usuarios (MS1).
     * No se usa @ManyToOne porque es una referencia a otro microservicio, 
     * por lo que solo guardamos el ID numérico.
     */
    @Column(name = "usuario_id")
    private Long usuarioId;

    /**
     * Almacena los parámetros en formato JSON.
     * Usamos columnDefinition = "TEXT" o "JSON" dependiendo de tu base de datos 
     * (PostgreSQL/MySQL) para asegurar que soporte cadenas largas.
     */
    @Column(name = "parametros", columnDefinition = "TEXT")
    private String parametros;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;
}