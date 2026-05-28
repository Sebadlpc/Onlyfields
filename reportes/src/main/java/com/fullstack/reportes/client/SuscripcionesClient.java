package com.fullstack.reportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-suscripciones", url = "${ms.suscripciones.url:http://ms-suscripciones:8084}", configuration = FeignClientConfig.class)
public interface SuscripcionesClient {

    @GetMapping("/api/v1/suscripciones/cliente/{clienteId}") // Asumiendo que puede obtener todas o por cliente
    List<Object> obtenerSuscripciones(@PathVariable("clienteId") Long clienteId);
}
