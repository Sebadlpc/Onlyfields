package com.fullstack.seguimiento.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaClienteDTO {
    private Long id;
    private Long clienteId;
    private String antecedentesMedicos;
    private String lesionesPrevias;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<MedicionCorporalDTO> mediciones;
}
