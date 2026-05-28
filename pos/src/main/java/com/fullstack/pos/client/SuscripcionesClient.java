package com.fullstack.pos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ms-suscripciones", url = "${ms.suscripciones.url:http://ms-suscripciones:8084}")
public interface SuscripcionesClient {

    @PutMapping("/api/v1/suscripciones/{id}/confirmar-pago")
    void confirmarPago(@PathVariable("id") Long id);
}
