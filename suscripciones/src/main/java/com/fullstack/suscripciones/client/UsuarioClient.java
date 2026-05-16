package com.fullstack.suscripciones.client;

import com.fullstack.suscripciones.dto.external.UsuarioExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "http://onlyfields-ms-usuarios:8080/api/v1/usuarios")
public interface UsuarioClient {

    @GetMapping("/{id}")
    UsuarioExternoDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}