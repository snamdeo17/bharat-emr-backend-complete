package com.bharatemr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve prescription PDFs
        registry.addResourceHandler("/prescriptions/**")
                .addResourceLocations("file:./prescriptions/");
        
        // Serve uploaded files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect root to Swagger UI
        registry.addRedirectViewController("/", "/swagger-ui.html");
        registry.addRedirectViewController("/api", "/swagger-ui.html");
    }
}