package com.pitsdog.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pitsDogOpenApi(){
        Server productionServer = new Server()
                .url("https://pitsdog-api-production.up.railway.app")
                .description("Servidor de produção - Railway");

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor local");

        return new OpenAPI()
                .servers(List.of(productionServer, localServer))
                .info(new Info()
                        .title("Pit's Dog API")
                        .description("API para gerenciamento de categorias, produtos, adicionais e pedidos do sistema Pit’s Dog.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Emanoel Cavalcante")));
    }
}
