package com.fullstack.suscripciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url:http://ms-usuarios:8081}")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/{id}")
    Object obtenerUsuarioPorId(@PathVariable("id") Long id);

    @PutMapping("/api/v1/usuarios/{id}/estado")
    void actualizarEstadoUsuario(@PathVariable("id") Long id, @RequestBody EstadoUsuarioDTO dto);

    record EstadoUsuarioDTO(String estado) {}
}
