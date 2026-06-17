package com.fullstack.reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map; // <-- Asegúrate de importar Map

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url:http://ms-usuarios:8081}")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/{id}")
    Map<String, Object> obtenerUsuarioPorId(@PathVariable("id") Long id); // Cambiamos Object por Map
}