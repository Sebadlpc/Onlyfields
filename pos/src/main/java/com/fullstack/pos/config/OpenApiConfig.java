package com.fullstack.pos.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configurarOpenApi() {
        Contact contacto = new Contact()
                .name("Sebastian De la Paz")
                .email("se.delapaz@duocuc.cl")
                .url("https://www.duoc.cl");

        License licencia = new License()
                .name("MIT")
                .url("https://opensource.org/licences/MIT");

        Info informacionApi = new Info()
                .title("OnlyFields API - Control de Accesos")
                .description("""
                        Sistema automatizado para la gestión de tokens QR,
                        auditoría de ingresos y control de aforo en vivo.
                        """)
                .version("1.0")
                .termsOfService("https://www.duoc.cl")
                .contact(contacto)
                .license(licencia);

        // Documentación externa (Recuerda cambiar esta URL por la de TU repositorio de GitHub grupal)
        ExternalDocumentation github = new ExternalDocumentation()
                .description("Repositorio oficial del proyecto en GitHub")
                .url("https://github.com/Sebadlpc/Onlyfields/tree/MainBCKP");

        // Configuración OpenAPI
        return new OpenAPI()
                .info(informacionApi)
                .externalDocs(github);
    }
}
