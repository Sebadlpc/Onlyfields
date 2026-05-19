package com.fullstack.pos.dto;

import com.fullstack.pos.Model.EstadoCaja;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CajaDTO {
    private Long id;
    private Long usuarioId;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Double montoInicial;
    private Float totalEfectivo;
    private Float totalTarjeta;
    private EstadoCaja estado;
}
