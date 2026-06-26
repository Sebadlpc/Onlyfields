package com.fullstack.reportes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_generado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa un reporte generado y almacenado en la base de datos")
public class ReporteGenerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del reporte", example = "1")
    private Long id;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Column(name = "tipo", nullable = false)
    @Schema(description = "Tipo de reporte generado", example = "FINANCIERO")
    private String tipo;

    @Column(name = "fecha_generacion")
    @Schema(description = "Fecha y hora en que se generó el reporte")
    private LocalDateTime fechaGeneracion;

    @Column(name = "usuario_id")
    @Schema(description = "ID del usuario que generó el reporte", example = "100")
    private Long usuarioId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parametros")
    @Schema(description = "Resultados del reporte en formato JSON", example = "{\\\"ingresosTotales\\\": 50000, \\\"canchasAlquiladas\\\": 12}")
    private String parametros;

    @Column(name = "ruta_archivo")
    @Schema(description = "Ruta de almacenamiento del archivo físico (si aplica)", example = "/docs/reportes/finanzas_2026.pdf")
    private String rutaArchivo;
}