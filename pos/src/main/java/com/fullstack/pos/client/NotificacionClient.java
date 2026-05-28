package com.fullstack.pos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url:http://ms-notificaciones:8090}")
public interface NotificacionClient {

    @PostMapping("/api/v1/notificaciones/enviar-comprobante")
    void enviarComprobante(@RequestBody ComprobantePagoDTO comprobante);

    // DTO anidado para la comunicación
    record ComprobantePagoDTO(Long transaccionId, String emailCliente, BigDecimal total) {}
}
