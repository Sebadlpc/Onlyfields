package com.fullstack.reportes.dto;

import com.fullstack.reportes.model.ReporteGenerado;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ReporteGeneradoResponseDTO {

    private Long id;
    private String tipo;
    private LocalDateTime fechaGeneracion;
    private Long usuarioId;
    private String parametros;
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