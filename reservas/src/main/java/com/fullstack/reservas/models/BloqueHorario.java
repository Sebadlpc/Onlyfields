package com.fullstack.reservas.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "bloque_horario")
@Data
public class BloqueHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mi_secuencia")
    @SequenceGenerator(name = "mi_secuencia", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;;

    @NotNull
    private LocalDateTime fechaInicio;

    @NotNull
    private LocalDateTime fechaFin;

    private String motivo;
}