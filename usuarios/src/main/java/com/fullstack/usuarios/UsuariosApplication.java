package com.fullstack.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicia la aplicación del microservicio de Usuarios.
 * Habilita la autoconfiguración de Spring Boot.
 */
@SpringBootApplication
public class UsuariosApplication {

	/**
	 * Punto de entrada principal para la aplicación Spring Boot.
	 * @param args Argumentos de línea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UsuariosApplication.class, args);
	}
}
