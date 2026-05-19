package com.fullstack.pos.client;

import com.fullstack.pos.dto.external.UsuarioExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios", url = "http://localhost:8080/api/v1/usuarios")
public interface UsuarioClient {
    @GetMapping("/{id}")
    UsuarioExternoDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}
