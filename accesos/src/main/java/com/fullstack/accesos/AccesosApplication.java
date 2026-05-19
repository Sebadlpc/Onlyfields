package com.fullstack.accesos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AccesosApplication {
	public static void main(String[] args) {
		SpringApplication.run(AccesosApplication.class, args);
	}
}
