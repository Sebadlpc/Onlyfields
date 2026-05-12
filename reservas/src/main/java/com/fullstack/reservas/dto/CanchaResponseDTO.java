package com.fullstack.reservas.dto;

import com.fullstack.reservas.models.Cancha;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CanchaResponseDTO {

    private Long id;
    private String nombre;
    private String deporte;
    private Integer capacidad;
    private BigDecimal tarifaHora;
    private String estado;

    public static CanchaResponseDTO fromEntity(Cancha cancha) {
        CanchaResponseDTO dto = new CanchaResponseDTO();
        dto.setId(cancha.getId());
        dto.setNombre(cancha.getNombre());
        dto.setDeporte(cancha.getDeporte());
        dto.setCapacidad(cancha.getCapacidad());
        dto.setTarifaHora(cancha.getTarifaHora());
        dto.setEstado(cancha.getEstado());
        return dto;
    }
}