package com.fullstack.suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal que inicia la aplicación del microservicio de Suscripciones.
 * Habilita la autoconfiguración de Spring Boot, el escaneo de clientes Feign y las tareas programadas.
 */
@SpringBootApplication
@EnableFeignClients // Habilita el uso de clientes Feign para la comunicación con otros microservicios.
@EnableScheduling // Habilita la ejecución de tareas programadas (@Scheduled).
public class SuscripcionesApplication {

	/**
	 * Punto de entrada principal para la aplicación Spring Boot.
	 * @param args Argumentos de línea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(SuscripcionesApplication.class, args);
	}
}
