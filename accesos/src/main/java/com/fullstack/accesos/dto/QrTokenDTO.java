package com.fullstack.accesos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos del token QR generado")
public class QrTokenDTO {

    @Schema(description = "Codigo unico del token")
    private String token;

    @Schema(description = "Fecha y hora de expiracion")
    private LocalDateTime fechaExpiracion;

    @Schema(description = "Indica si el token ya fue escaneado")
    private boolean usado;
}
