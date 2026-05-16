package com.fullstack.accesos.model;

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

public class QrToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotBlank(message = "El token no puede estar vacio")
    @Column(name = "token", nullable = false, unique = true,length = 500)
    private String token;

    @NotNull(message = "La fecha de expiracion es obligatoria")
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Builder.Default
    @Column(name = "usado", nullable = false)
    private boolean usado = false;
}
