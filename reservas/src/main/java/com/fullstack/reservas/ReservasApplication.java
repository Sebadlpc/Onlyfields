package com.fullstack.reservas; 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = { 
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class
})
@EnableScheduling //  Habilita las tareas en segundo plano (limpieza de reservas)
public class ReservasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasApplication.class, args);
    }

    @Bean // Nos permite inyectar RestTemplate en el Service para llamar a otros microservicios
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}