package com.fullstack.seguimiento.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicion_corporal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicionCorporal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_cliente_id", nullable = false)
    private FichaCliente fichaCliente;

    @Column(nullable = false)
    private LocalDateTime fechaMedicion;

    private Double peso;
    private Double altura;
    private Double porcentajeGrasa;
    private Double masaMuscular;
    private Double perimetroCintura;
    private Double perimetroCadera;

    @Enumerated(EnumType.STRING)
    private ObjetivoFisico objetivoActual;

    @PrePersist
    protected void onCreate() {
        if (fechaMedicion == null) {
            fechaMedicion = LocalDateTime.now();
        }
    }
}
