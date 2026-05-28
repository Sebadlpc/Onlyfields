package com.fullstack.reportes.client;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfig {

    @Bean
    public Request.Options requestOptions() {
        // 5 segundos para conectar, 30 segundos para leer
        return new Request.Options(5, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }
}
