package com.fullstack.seguimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SeguimientoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeguimientoApplication.class, args);
	}
}
