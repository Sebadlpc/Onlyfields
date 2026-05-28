package com.fullstack.reservas.dto;

import com.fullstack.reservas.models.BloqueHorario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BloqueHorarioResponseDTO {

    private Long id;
    private Long canchaId;
    private String canchaNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String motivo;
    public static BloqueHorarioResponseDTO fromEntity(BloqueHorario bloque) {
        BloqueHorarioResponseDTO dto = new BloqueHorarioResponseDTO();
        dto.setId(bloque.getId());
        dto.setFechaInicio(bloque.getFechaInicio());
        dto.setFechaFin(bloque.getFechaFin());
        dto.setMotivo(bloque.getMotivo());

        if (bloque.getCancha() != null) {
            dto.setCanchaId(bloque.getCancha().getId());
            dto.setCanchaNombre(bloque.getCancha().getNombre());
        }

        return dto;
    }
}