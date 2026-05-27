package com.fullstack.suscripciones.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class SuscripcionResponseDTO {
    private Long id;
    private Long clienteId;
    private Long planId;
    private String planNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer diasCongelados;
}