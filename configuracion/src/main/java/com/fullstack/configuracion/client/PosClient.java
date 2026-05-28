package com.fullstack.configuracion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "ms-pos", url = "${ms.pos.url:http://ms-pos:8083}")
public interface PosClient {

    @PutMapping("/api/v1/pos/configuracion/tarifa")
    void actualizarTarifa(@RequestBody TarifaDTO tarifa);

    record TarifaDTO(String clave, BigDecimal valor) {}
}
