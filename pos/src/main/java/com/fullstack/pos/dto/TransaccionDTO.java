package com.fullstack.pos.dto;

import com.fullstack.pos.Model.EstadoTransaccion;
import com.fullstack.pos.Model.MetodoPago;
import com.fullstack.pos.Model.TipoTransaccion;
import lombok.*;
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
    private Double total;
    private MetodoPago metodoPago;
    private EstadoTransaccion estado;
    private LocalDateTime fechaHora;
    private List<ItemTransaccionDTO> items;
}
