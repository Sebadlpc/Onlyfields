package com.fullstack.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8085").description("Servidor Local - Inventario")))
                .info(new Info()
                        .title("API 2026 Reservas de salas")
                        .version("1.0")
                        .description("Documentación de la API para el sistema de reserva de salas"));
    }
}
