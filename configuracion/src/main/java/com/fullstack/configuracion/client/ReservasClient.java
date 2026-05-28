package com.fullstack.configuracion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-reservas", url = "${ms.reservas.url:http://ms-reservas:8082}")
public interface ReservasClient {

    @PutMapping("/api/v1/reservas/configuracion/horario")
    void actualizarHorario(@RequestBody HorarioDTO horario);

    record HorarioDTO(String horarioApertura, String horarioCierre) {}
}
