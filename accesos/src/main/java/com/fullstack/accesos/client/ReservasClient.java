package com.fullstack.accesos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-reservas", url = "${ms.reservas.url:http://ms-reservas:8082}")
public interface ReservasClient {

    @GetMapping("/api/v1/reservas/cliente/{clienteId}")
    List<ReservaDTO> obtenerReservasPorCliente(@PathVariable("clienteId") Long clienteId);

    // Assuming the DTO from Reservas has an 'estado' field
    record ReservaDTO(String estado, java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin) {}
}
