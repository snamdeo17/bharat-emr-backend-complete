package com.bharatemr.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bharat EMR API")
                        .version("1.0.0")
                        .description("Electronic Medical Records System for Indian Healthcare Providers\n\n" +
                                "## Features\n" +
                                "- OTP-based authentication\n" +
                                "- Doctor and Patient management\n" +
                                "- Visit and Prescription tracking\n" +
                                "- PDF prescription generation\n" +
                                "- Follow-up scheduling\n" +
                                "- SMS/WhatsApp notifications\n\n" +
                                "## Authentication\n" +
                                "All protected endpoints require JWT token in Authorization header:\n" +
                                "`Authorization: Bearer <token>`")
                        .contact(new Contact()
                                .name("Bharat EMR Support")
                                .email("support@bharatemr.com")
                                .url("https://bharatemr.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.bharatemr.com")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from login/register endpoints")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}