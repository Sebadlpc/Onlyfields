package com.fullstack.reportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-inventario", url = "${ms.inventario.url:http://ms-inventario:8085}", configuration = FeignClientConfig.class)
public interface InventarioClient {

    @GetMapping("/api/v1/productos")
    List<Object> obtenerProductos();
}
