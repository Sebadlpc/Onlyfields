package com.fullstack.seguimiento.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "mediciones_corporales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MedicionCorporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private FichaCliente ficha;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Positive
    @Column(name = "peso", nullable = false)
    private Double peso;

    @Positive
    @Column(name = "altura", nullable = false)
    private Double altura;

    @Column(name = "imc")
    private Double imc;

    @Positive
    @Column(name = "cintura")
    private Double cintura;

    @Positive
    @Column(name = "cadera")
    private Double cadera;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo")
    private ObjetivoFisico objetivo;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDate.now();
        this.imc = calcularIMC();
    }

    public Double calcularIMC() {
        if (this.peso != null && this.altura != null && this.altura > 0){
            return Math.round(this.peso / (this.altura*this.altura)) * 100.0 / 100.0;
        }
        return null;
    }
}
