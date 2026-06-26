package com.fullstack.reportes.dto;

import com.fullstack.reportes.model.ReporteGenerado;
import io.swagger.v3.oas.annotations.media.Schema; // <- IMPORTANTE
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Objeto de petición para generar un nuevo reporte")
public class ReporteGeneradoRequestDTO {

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Schema(description = "Tipo de reporte a generar", example = "FINANCIERO", allowableValues = {"FINANCIERO", "INVENTARIO", "RESERVAS"})
    private String tipo;

    @Schema(description = "ID del usuario que solicita el reporte", example = "100")
    private Long usuarioId;

    @Schema(description = "Ruta donde se guardará el archivo físico (opcional)", example = "/docs/reportes/finanzas.pdf")
    private String rutaArchivo;

    @Schema(description = "Parámetros adicionales en formato JSON", example = "{\"mes\": \"Mayo\", \"año\": 2026}")
    private String parametros;

    // Mapeo DTO → Entidad
    public ReporteGenerado toEntity() {
        return ReporteGenerado.builder()
                .tipo(this.tipo)
                .usuarioId(this.usuarioId)
                .parametros(this.parametros)
                .rutaArchivo(this.rutaArchivo)
                .build();
    }
}