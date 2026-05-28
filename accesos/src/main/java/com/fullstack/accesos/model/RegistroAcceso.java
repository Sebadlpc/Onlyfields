package com.fullstack.accesos.model;

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

public class RegistroAcceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoAcceso tipo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false)
    private ResultadoAcceso resultado;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
