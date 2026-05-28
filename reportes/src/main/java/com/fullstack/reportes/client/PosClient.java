package com.fullstack.reportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-pos", url = "${ms.pos.url:http://ms-pos:8083}", configuration = FeignClientConfig.class)
public interface PosClient {

    @GetMapping("/api/v1/pos/transacciones")
    List<Object> obtenerTransacciones(); // Using Object for simplicity
}
