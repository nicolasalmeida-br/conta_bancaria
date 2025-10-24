package com.senai.conta_bancaria.infrastructure.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI contaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Conta Bancária")
                        .description("Cadastro e gestão de serviços de uma conta bancária.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Equipe Bancária")
                                .email("suporte@banco.com"))
                );
    }
}