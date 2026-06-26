package com.fullstack.seguimiento.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ficha_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad de la ficha médica y técnica del cliente")
public class FichaCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la ficha", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "ID del cliente asociado", example = "105")
    private Long clienteId;

    @Schema(description = "Historial médico del socio", example = "Hipertensión leve")
    private String antecedentesMedicos;

    @Schema(description = "Lesiones o cirugías previas", example = "Operación de meniscos en rodilla izquierda")
    private String lesionesPrevias;

    @Schema(description = "Notas adicionales del evaluador", example = "Evitar cargas extremas en tren inferior")
    private String observaciones;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de registro de la ficha", example = "2026-06-11T13:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "fichaCliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Listado histórico de controles corporales")
    private List<MedicionCorporal> mediciones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
