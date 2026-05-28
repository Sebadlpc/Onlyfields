package com.fullstack.reportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-reservas", url = "${ms.reservas.url:http://ms-reservas:8082}", configuration = FeignClientConfig.class)
public interface ReservasClient {

    @GetMapping("/api/v1/reservas")
    List<Object> obtenerReservas();
}
