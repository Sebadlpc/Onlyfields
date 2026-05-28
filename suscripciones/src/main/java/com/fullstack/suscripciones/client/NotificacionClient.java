package com.fullstack.suscripciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url:http://ms-notificaciones:8090}")
public interface NotificacionClient {

    @PostMapping("/api/v1/notificaciones/enviar")
    void enviarNotificacion(@RequestBody NotificacionDTO notificacion);

    record NotificacionDTO(Long clienteId, String tipo, String mensaje) {}
}
