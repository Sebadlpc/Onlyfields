package com.fullstack.accesos.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrTokenDTO {
    private String token;
    private LocalDateTime fechaExpiracion;
    private boolean usado;
}
