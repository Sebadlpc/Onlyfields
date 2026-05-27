package com.fullstack.configuracion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ConfiguracionApplication
{
	public static void main(String[] args) {
		SpringApplication.run(ConfiguracionApplication.class, args);
	}
}