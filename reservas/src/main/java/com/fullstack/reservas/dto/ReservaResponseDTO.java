package com.fullstack.reservas.dto;

import com.fullstack.reservas.models.Reserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class ReservaResponseDTO {

    private Long id;
    private Long clienteId;

   
    private Long canchaId;
    private String canchaNombre;
    private String canchaDeporte;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private BigDecimal totalCobrado;

  
    // Mapeo DTO

    public static ReservaResponseDTO fromEntity(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();

        dto.setId(reserva.getId());
        dto.setClienteId(reserva.getClienteId());
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setEstado(reserva.getEstado());
        dto.setTotalCobrado(reserva.getTotalCobrado());

        
        if (reserva.getCancha() != null) {
            dto.setCanchaId(reserva.getCancha().getId());
            dto.setCanchaNombre(reserva.getCancha().getNombre());
            dto.setCanchaDeporte(reserva.getCancha().getDeporte());
        }

        return dto;
    }
}