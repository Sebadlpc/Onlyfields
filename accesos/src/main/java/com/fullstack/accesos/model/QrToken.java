package com.fullstack.accesos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entidad del token QR de acceso")

public class QrToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID unico en BD", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    @Schema(description = "ID del cliente asociado", example = "1")
    private Long clienteId;

    @NotBlank(message = "El token no puede estar vacio")
    @Column(name = "token", nullable = false, unique = true,length = 500)
    @Schema(description = "Codigo hash del token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @NotNull(message = "La fecha de expiracion es obligatoria")
    @Column(name = "fecha_expiracion", nullable = false)
    @Schema(description = "Fecha de vencimiento", example = "2026-06-11T12:45:00")
    private LocalDateTime fechaExpiracion;

    @Builder.Default
    @Column(name = "usado", nullable = false)
    @Schema(description = "Estado de uso del QR", example = "false")
    private boolean usado = false;
}