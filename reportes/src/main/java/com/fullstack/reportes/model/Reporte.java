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

  
    @Column(name = "usuario_id")
    private Long usuarioId;


    @Column(name = "parametros", columnDefinition = "TEXT")
    private String parametros;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;
}