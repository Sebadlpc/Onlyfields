package com.fullstack.suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // Importante

@SpringBootApplication
@EnableFeignClients // Esta es la instrucción que te falta
public class SuscripcionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuscripcionesApplication.class, args);
    }

}