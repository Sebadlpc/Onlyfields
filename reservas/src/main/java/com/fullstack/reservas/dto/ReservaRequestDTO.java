package com.fullstack.reservas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequestDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long canchaId;

    @NotNull
    @Future
    private LocalDateTime fechaInicio;

    @NotNull
    @Future
    private LocalDateTime fechaFin;
}
