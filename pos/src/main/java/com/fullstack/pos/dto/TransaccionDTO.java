package com.fullstack.pos.dto;

import com.fullstack.pos.model.EstadoTransaccion;
import com.fullstack.pos.model.MetodoPago;
import com.fullstack.pos.model.TipoTransaccion;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransaccionDTO {
    private Long id;
    private Long cajaId;
    private Long clienteId;
    private TipoTransaccion tipo;
    private BigDecimal total;
    private MetodoPago metodoPago;
    private EstadoTransaccion estado;
    private LocalDateTime fechaHora;
    private List<ItemTransaccionDTO> items;
}
