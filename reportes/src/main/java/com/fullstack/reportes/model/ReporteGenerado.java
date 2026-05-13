package com.fullstack.reportes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class ReporteGenerado {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    
    @Column(name = "usuario_id")
    private Long usuarioId;

    
    @Lob
    @Column(name = "parametros")
    private String parametros;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;
}