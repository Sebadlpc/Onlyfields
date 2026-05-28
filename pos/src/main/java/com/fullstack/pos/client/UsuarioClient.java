package com.fullstack.pos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url:http://ms-usuarios:8081}")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/{id}")
    void obtenerUsuarioPorId(@PathVariable("id") Long id);
}
