package com.fullstack.accesos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-suscripciones", url = "${ms.suscripciones.url:http://ms-suscripciones:8084}")
public interface SuscripcionesClient {

    @GetMapping("/api/v1/suscripciones/cliente/{clienteId}")
    List<SuscripcionDTO> obtenerSuscripcionesPorCliente(@PathVariable("clienteId") Long clienteId);

    record SuscripcionDTO(String estado) {}
}
