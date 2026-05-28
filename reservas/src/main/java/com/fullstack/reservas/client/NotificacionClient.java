package com.fullstack.reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// El nombre "ms-notificaciones" debe coincidir con el nombre del servicio en docker-compose.yml
@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url:http://ms-notificaciones:8090}")
public interface NotificacionClient {

    @PostMapping("/api/v1/notificaciones/enviar-comprobante")
    void enviarComprobante(@RequestBody NotificacionReservaDTO notificacion);

    // DTO anidado para la comunicación
    record NotificacionReservaDTO(Long reservaId, String emailCliente, String mensaje) {}
}
