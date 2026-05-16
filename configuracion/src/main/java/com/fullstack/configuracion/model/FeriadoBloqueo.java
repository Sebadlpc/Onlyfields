package com.fullstack.configuracion.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "feriados_bloqueos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeriadoBloqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "afecta_reservas", nullable = false)
    private Boolean afectaReservas;
}
