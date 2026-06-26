package com.fullstack.reportes.dto;

import com.fullstack.reportes.model.ReporteGenerado;
import io.swagger.v3.oas.annotations.media.Schema; // <- IMPORTANTE
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Objeto de respuesta que representa un reporte ya generado")
public class ReporteGeneradoResponseDTO {

    @Schema(description = "ID único del reporte en la base de datos", example = "1")
    private Long id;

    @Schema(description = "Tipo de reporte generado", example = "FINANCIERO")
    private String tipo;

    @Schema(description = "Fecha y hora exacta en la que se generó", example = "2026-05-28T20:45:00")
    private LocalDateTime fechaGeneracion;

    @Schema(description = "ID del usuario que lo generó", example = "100")
    private Long usuarioId;

    @Schema(description = "Resultados del reporte (JSON)", example = "{\"ingresosTotales\": 50000, \"canchasAlquiladas\": 12}")
    private String parametros;

    @Schema(description = "Ruta de almacenamiento", example = "Cálculo en vivo (Sin archivo físico)")
    private String rutaArchivo;

    public static ReporteGeneradoResponseDTO fromEntity(ReporteGenerado reporte) {
        ReporteGeneradoResponseDTO dto = new ReporteGeneradoResponseDTO();
        dto.setId(reporte.getId());
        dto.setTipo(reporte.getTipo());
        dto.setFechaGeneracion(reporte.getFechaGeneracion());
        dto.setUsuarioId(reporte.getUsuarioId());
        dto.setParametros(reporte.getParametros());
        dto.setRutaArchivo(reporte.getRutaArchivo());
        return dto;
    }
}