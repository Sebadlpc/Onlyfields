package com.fullstack.reportes.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaExternaDTO {
    private Long id;
    private Long canchaId;
    private Long clienteId;
    private String estado;
    private Double totalCobrado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}