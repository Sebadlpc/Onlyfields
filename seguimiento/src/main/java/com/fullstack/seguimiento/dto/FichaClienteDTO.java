package com.fullstack.seguimiento.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FichaClienteDTO {
    private Long id;
    private Long clienteId;
    private String lesiones;
    private String observaciones;
    private LocalDate fechaIngreso;
    private List<MedicionCorporalDTO> mediciones; // Usa la versión DTO para cortar el bucle
}