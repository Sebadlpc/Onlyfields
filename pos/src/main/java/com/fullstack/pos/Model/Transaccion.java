package com.fullstack.pos.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Normalized;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "caja_id", nullable = false)
    private Long cajaId;

    @NotNull
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransaccion tipo;

    @Positive
    @Column(name = "total", nullable = false)
    private Double total;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTransaccion estado;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @OneToMany(mappedBy = "transaccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTransaccion> items;

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
        this.estado = EstadoTransaccion.COMPLETADA;
    }
}
