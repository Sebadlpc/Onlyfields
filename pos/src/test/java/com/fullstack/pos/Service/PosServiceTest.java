package com.fullstack.pos.Service;

import com.fullstack.pos.client.InventarioClient;
import com.fullstack.pos.client.NotificacionClient;
import com.fullstack.pos.client.SuscripcionesClient;
import com.fullstack.pos.client.UsuarioClient;
import com.fullstack.pos.dto.CajaDTO;
import com.fullstack.pos.dto.ItemTransaccionDTO;
import com.fullstack.pos.dto.TransaccionDTO;
import com.fullstack.pos.exception.CajaException;
import com.fullstack.pos.exception.TransaccionException;
import com.fullstack.pos.model.*;
import com.fullstack.pos.Repository.CajaRepository;
import com.fullstack.pos.Repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosServiceTest {

    @Mock private CajaRepository cajaRepository;
    @Mock private TransaccionRepository transaccionRepository;
    @Mock private UsuarioClient usuarioClient;
    @Mock private InventarioClient inventarioClient;
    @Mock private SuscripcionesClient suscripcionesClient;
    @Mock private NotificacionClient notificacionClient;

    @InjectMocks private com.fullstack.pos.Service.PosService posService;

    private Caja cajaAbiertaMock;

    @BeforeEach
    void setUp() {
        cajaAbiertaMock = Caja.builder()
                .id(1L)
                .usuarioId(5L)
                .montoInicial(BigDecimal.valueOf(50000))
                .totalEfectivo(BigDecimal.ZERO)
                .totalTarjeta(BigDecimal.ZERO)
                .estado(EstadoCaja.ABIERTA)
                .build();
    }

    @Test
    @DisplayName("Debe abrir una caja exitosamente si no hay otra abierta")
    void abrirCaja_Exito() {
        when(cajaRepository.findByEstado(EstadoCaja.ABIERTA)).thenReturn(Optional.empty());
        when(cajaRepository.save(any(Caja.class))).thenReturn(cajaAbiertaMock);

        CajaDTO resultado = posService.abrirCaja(5L, 50000.0);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoCaja.ABIERTA);
        verify(cajaRepository).save(any(Caja.class));
    }

    @Test
    @DisplayName("Debe lanzar CajaException si ya existe una caja abierta")
    void abrirCaja_YaAbierta_LanzaExcepcion() {
        when(cajaRepository.findByEstado(EstadoCaja.ABIERTA)).thenReturn(Optional.of(cajaAbiertaMock));

        assertThatThrownBy(() -> posService.abrirCaja(5L, 50000.0))
                .isInstanceOf(CajaException.class)
                .hasMessageContaining("Ya existe una caja abierta");
    }

    @Test
    @DisplayName("Debe cerrar la caja actual y pasar su estado a CERRADA")
    void cerrarCaja_Exito() {
        when(cajaRepository.findByEstado(EstadoCaja.ABIERTA)).thenReturn(Optional.of(cajaAbiertaMock));
        when(cajaRepository.save(any(Caja.class))).thenAnswer(i -> i.getArgument(0));

        CajaDTO resultado = posService.cerrarCaja();

        assertThat(resultado.getEstado()).isEqualTo(EstadoCaja.CERRADA);
        assertThat(resultado.getFechaCierre()).isNotNull();
    }

    @Test
    @DisplayName("Debe procesar una venta en EFECTIVO correctamente, descontando inventario")
    void registrarTransaccion_VentaEfectivo_Exito() {
        when(cajaRepository.findByEstado(EstadoCaja.ABIERTA)).thenReturn(Optional.of(cajaAbiertaMock));

        ItemTransaccionDTO itemDto = ItemTransaccionDTO.builder()
                .productoId(101L).cantidad(2).precioUnitario(BigDecimal.valueOf(1500)).descripcion("Bebida").build();

        TransaccionDTO transaccionDto = TransaccionDTO.builder()
                .clienteId(10L)
                .tipo(TipoTransaccion.VENTA)
                .metodoPago(MetodoPago.EFECTIVO)
                .total(BigDecimal.valueOf(3000))
                .items(List.of(itemDto))
                .build();

        Transaccion transaccionGuardada = Transaccion.builder()
                .id(1L)
                .cajaId(1L)
                .tipo(TipoTransaccion.VENTA)
                .metodoPago(MetodoPago.EFECTIVO)
                .total(BigDecimal.valueOf(3000))
                .items(List.of(ItemTransaccion.builder().productoId(101L).cantidad(2).precioUnitario(BigDecimal.valueOf(1500)).build()))
                .build();

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionGuardada);

        TransaccionDTO resultado = posService.registrarTransaccion(transaccionDto);

        assertThat(resultado).isNotNull();
        verify(inventarioClient, times(1)).actualizarStock(eq(101L), any());
        verify(notificacionClient, times(1)).enviarComprobante(any());
        assertThat(cajaAbiertaMock.getTotalEfectivo()).isEqualByComparingTo("3000");
    }

    @Test
    @DisplayName("Debe procesar un PAGO_SUSCRIPCION y confirmar el pago en el microservicio externo")
    void registrarTransaccion_PagoSuscripcion_Exito() {
        when(cajaRepository.findByEstado(EstadoCaja.ABIERTA)).thenReturn(Optional.of(cajaAbiertaMock));

        ItemTransaccionDTO itemDto = ItemTransaccionDTO.builder()
                .productoId(500L).cantidad(1).precioUnitario(BigDecimal.valueOf(29990)).descripcion("Plan Mensual").build();

        TransaccionDTO transaccionDto = TransaccionDTO.builder()
                .tipo(TipoTransaccion.PAGO_SUSCRIPCION)
                .metodoPago(MetodoPago.TARJETA)
                .total(BigDecimal.valueOf(29990))
                .items(List.of(itemDto))
                .build();

        Transaccion transaccionGuardada = Transaccion.builder()
                .id(2L)
                .cajaId(1L)
                .tipo(TipoTransaccion.PAGO_SUSCRIPCION)
                .metodoPago(MetodoPago.TARJETA)
                .total(BigDecimal.valueOf(29990))
                .items(List.of(ItemTransaccion.builder().productoId(500L).cantidad(1).precioUnitario(BigDecimal.valueOf(29990)).build()))
                .build();

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionGuardada);

        posService.registrarTransaccion(transaccionDto);

        verify(suscripcionesClient, times(1)).confirmarPago(500L);
        assertThat(cajaAbiertaMock.getTotalTarjeta()).isEqualByComparingTo("29990");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la transacción no tiene ítems")
    void registrarTransaccion_SinItems_LanzaExcepcion() {
        when(cajaRepository.findByEstado(EstadoCaja.ABIERTA)).thenReturn(Optional.of(cajaAbiertaMock));
        TransaccionDTO transaccionDto = TransaccionDTO.builder().items(Collections.emptyList()).build();

        assertThatThrownBy(() -> posService.registrarTransaccion(transaccionDto))
                .isInstanceOf(TransaccionException.class)
                .hasMessageContaining("al menos un ítem");
    }
}