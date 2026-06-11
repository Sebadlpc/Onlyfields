package com.fullstack.seguimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad de registros y evolución física")
public class MedicionCorporal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del control", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_cliente_id", nullable = false)
    @Schema(hidden = true)
    private FichaCliente fichaCliente;

    @Column(nullable = false)
    @Schema(description = "Fecha del control físico", example = "2026-06-11T13:05:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaMedicion;

    @Schema(description = "Peso en kilogramos", example = "78.5")
    private Double peso;
    @Schema(description = "Altura en metros", example = "1.75")
    private Double altura;
    @Schema(description = "Porcentaje de grasa corporal", example = "14.2")
    private Double porcentajeGrasa;
    @Schema(description = "Masa muscular en kilogramos", example = "36.8")
    private Double masaMuscular;
    @Schema(description = "Perímetro de cintura en centímetros", example = "82.0")
    private Double perimetroCintura;
    @Schema(description = "Perímetro de cadera en centímetros", example = "94.5")
    private Double perimetroCadera;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Meta física actual del socio", example = "HIPERTROFIA")
    private ObjetivoFisico objetivoActual;

    @PrePersist
    protected void onCreate() {
        if (fechaMedicion == null) {
            fechaMedicion = LocalDateTime.now();
        }
    }
}
