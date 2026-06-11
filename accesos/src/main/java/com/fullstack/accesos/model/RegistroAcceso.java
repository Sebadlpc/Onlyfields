package com.fullstack.accesos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_acceso")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entidad de historial de accesos")

public class RegistroAcceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único en BD", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull
    @Column(name = "cliente_id", nullable = false)
    @Schema(description = "ID del cliente", example = "1")
    private Long clienteId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, columnDefinition = "varchar(20)")
    @Schema(description = "Tipo de movimiento", example = "ENTRADA")
    private TipoAcceso tipo;

    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora del evento", example = "2026-06-11T12:36:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, columnDefinition = "varchar(20)")
    @Schema(description = "Resultado del acceso", example = "PERMITIDO")
    private ResultadoAcceso resultado;

    @Column(name = "motivo_rechazo")
    @Schema(description = "Causa de la denegacion", example = "Token invalido")
    private String motivoRechazo;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
