package com.fullstack.accesos.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAccesoDTO {
    private Long clienteId;
    private String tipo; // ENTRADA o SALIDA
    private String resultado; // PERMITIDO o DENEGADO
    private String motivoRechazo;
    private LocalDateTime fechaHora;
}