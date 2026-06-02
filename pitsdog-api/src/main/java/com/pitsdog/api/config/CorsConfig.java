package com.pitsdog.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigure() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedHeaders(
                                "http://localhost:5137",
                                "http://localhost:3000",
                                "http://pitsdog-site.netlify.app"
                        )
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"
                        )
                        .allowedHeaders(
                                "Content-Type",
                                "Authorization"
                        )
                        .maxAge(3600);
            }
        };
    }
}
