package com.fullstack.accesos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen del resultado de un acceso")
public class RegistroAccesoDTO {

    @Schema(description = "ID del cliente")
    private Long clienteId;

    @Schema(description = "Tipo de movimiento")
    private String tipo; // ENTRADA o SALIDA

    @Schema(description = "Resultado del acceso")
    private String resultado; // PERMITIDO o DENEGADO

    @Schema(description = "Detalle del rechazo si el resultado es DENEGADO")
    private String motivoRechazo;

    @Schema(description = "Fecha y hora excata del evento")
    private LocalDateTime fechaHora;
}