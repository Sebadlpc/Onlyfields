package com.fullstack.reportes.dto;

import com.fullstack.reportes.model.ReporteGenerado;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ReporteGeneradoRequestDTO {

    @NotBlank(message = "El tipo de reporte es obligatorio")
    private String tipo;

    private Long usuarioId;

    private String rutaArchivo;
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