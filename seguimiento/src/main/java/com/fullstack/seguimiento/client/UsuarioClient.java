package com.fullstack.seguimiento.client;

import com.fullstack.seguimiento.dto.external.UsuarioExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: nombre del servicio en docker, url: ruta base local para pruebas
@FeignClient(name = "usuarios", url = "http://localhost:8080/api/v1/usuarios")
public interface UsuarioClient {

    @GetMapping("/{id}")
    UsuarioExternoDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}
