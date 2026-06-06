package com.pitsdog.api.config.ratelimit;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RateLimitProperties properties;

    private final Cache<String, Bucket> buckets;

    public RateLimitService(RateLimitProperties properties) {
        this.properties = properties;
        validarConfiguracoes();

        this.buckets = Caffeine.newBuilder()
                .maximumSize(properties.getCache().getMaximoRegistros())
                .expireAfterAccess(
                        properties.getCache().getExpiracaoMinutos(),
                        TimeUnit.MINUTES
                )
                .build();
    }

    public ConsumptionProbe consumir(RateLimitTipo tipo, String identificador){
        String chave = tipo.name() + ":" + identificador;

        Bucket bucket = buckets.get(
                chave,
                key -> criarBucket(tipo)
        );

        return bucket.tryConsumeAndReturnRemaining(1);
    }

    private Bucket criarBucket(RateLimitTipo tipo){
        RateLimitProperties.Regra regra = switch(tipo){
            case LOGIN -> properties.getLogin();
            case CONSULTA_PUBLICA -> properties.getConsultaPublica();
        };

        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(regra.getCapacidade())
                        .refillGreedy(
                                regra.getCapacidade(),
                                Duration.ofMinutes(regra.getPeriodoMinutos())
                        )
                )
                .build();
    }


    private void validarRegra(
            RateLimitTipo tipo,
            RateLimitProperties.Regra regra
    ){
        if(regra.getCapacidade() <= 0){
            throw new IllegalStateException(
                    "Capacidade inválida no rate limit: " + tipo
            );
        }

        if(regra.getPeriodoMinutos() <= 0){
            throw new IllegalStateException(
                    "Período inválido no rate limit: " + tipo
            );
        }
    }

    private void validarConfiguracoes(){

        validarRegra(RateLimitTipo.LOGIN, properties.getLogin());
        validarRegra(RateLimitTipo.CONSULTA_PUBLICA, properties.getConsultaPublica());


        if(properties.getCache().getExpiracaoMinutos() <= 0){
            throw new IllegalStateException(
                    "app.rate-limit.cache.expiracao-minutos deve ser maior que zero."
            );
        }

        if(properties.getCache().getMaximoRegistros() <= 0){
            throw new IllegalStateException(
                    "app.rate-limit.cache.maximo-registros deve ser maior que zero."
            );
        }
    }
}
