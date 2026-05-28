package com.fullstack.pos.dto;

import com.fullstack.pos.model.EstadoCaja;
import lombok.*;
import java.math.BigDecimal;
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
    private BigDecimal montoInicial;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTarjeta;
    private EstadoCaja estado;
}
