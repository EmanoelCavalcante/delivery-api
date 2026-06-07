package com.pitsdog.api.config.ratelimit;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private Regra login = new Regra();

    private Regra consultaPublica = new Regra();

    private Regra pedidoPublico = new Regra();

    private Regra admin = new Regra();

    private ConfiguracaoCache cache = new ConfiguracaoCache();

    @Getter
    @Setter
    public static class Regra{

        private long capacidade;

        private long periodoMinutos;
    }

    @Getter
    @Setter
    public static class ConfiguracaoCache{

        private long expiracaoMinutos;

        private long maximoRegistros;
    }
}

