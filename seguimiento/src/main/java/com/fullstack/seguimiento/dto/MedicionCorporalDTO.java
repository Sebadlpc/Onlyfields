package com.fullstack.seguimiento.dto;

import com.fullstack.seguimiento.model.ObjetivoFisico;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicionCorporalDTO {
    private Long id;
    private Long fichaId;
    private LocalDateTime fechaMedicion;
    private Double peso;
    private Double altura;
    private Double porcentajeGrasa;
    private Double masaMuscular;
    private Double perimetroCintura;
    private Double perimetroCadera;
    private Double imc;
    private ObjetivoFisico objetivoActual;
}
