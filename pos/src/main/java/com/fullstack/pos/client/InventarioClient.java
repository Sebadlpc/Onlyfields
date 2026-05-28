package com.fullstack.pos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ms-inventario", url = "${ms.inventario.url:http://ms-inventario:8085}")
public interface InventarioClient {

    @PutMapping("/api/v1/productos/{id}/stock")
    void actualizarStock(@PathVariable("id") Long productoId, @RequestBody MovimientoStockDTO dto);

    // DTO anidado para la comunicación
    record MovimientoStockDTO(String tipo, Integer cantidad, String referencia) {}
}
