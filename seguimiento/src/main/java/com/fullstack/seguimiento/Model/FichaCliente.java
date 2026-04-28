package com.fullstack.seguimiento.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "fichas_clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FichaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "cliente_id", nullable = false, unique = true)
    private Long clienteId;

    @Column(name = "lesiones", columnDefinition = "TEXT")
    private String lesiones;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @OneToMany(mappedBy = "ficha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicionCorporal> mediciones;

    @PrePersist
    public void prePersist() {
        this.fechaIngreso = LocalDate.now();
    }
}
