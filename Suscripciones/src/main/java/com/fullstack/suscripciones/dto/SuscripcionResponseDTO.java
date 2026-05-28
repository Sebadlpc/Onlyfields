package com.fullstack.suscripciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
