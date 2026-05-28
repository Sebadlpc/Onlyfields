package com.fullstack.configuracion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal que inicia la aplicación del microservicio de Configuración.
 * Habilita la autoconfiguración de Spring Boot y la búsqueda de clientes Feign.
 */
@SpringBootApplication
@EnableFeignClients // Habilita el escaneo de clientes Feign para la comunicación entre servicios.
public class ConfiguracionApplication
{
	public static void main(String[] args) {
		SpringApplication.run(ConfiguracionApplication.class, args);
	}
}
