package com.fullstack.reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

// El nombre "ms-pos" debe coincidir con el nombre del servicio en docker-compose.yml
@FeignClient(name = "ms-pos", url = "${ms.pos.url:http://ms-pos:8083}")
public interface PosClient {

    // Asumo que ms-pos tendrá un DTO para recibir este cobro
    // y un endpoint como /api/v1/pos/cobros
    @PostMapping("/api/v1/pos/cobros")
    void generarCobro(@RequestBody CobroReservaDTO cobro);
    
    // DTO anidado para la comunicación
    record CobroReservaDTO(Long reservaId, Long clienteId, BigDecimal monto) {}
}
