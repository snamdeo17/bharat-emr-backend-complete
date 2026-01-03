package com.bharatemr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class BharatEmrApplication {
    public static void main(String[] args) {
        SpringApplication.run(BharatEmrApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("Bharat EMR Backend Started Successfully!");
        System.out.println("API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("===========================================\n");
    }
}