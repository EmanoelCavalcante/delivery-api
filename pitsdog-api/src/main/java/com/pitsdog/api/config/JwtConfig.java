package com.pitsdog.api.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    private final String jwtSecret;

    public JwtConfig(@Value("${JWT_SECRET}") String jwtSecret){
        this.jwtSecret = jwtSecret;
    }

    @PostConstruct
    public void validarSecret(){
        if(jwtSecret == null || jwtSecret.isBlank()){
            throw new IllegalStateException(
                    "JWT_SECRET não pode estar vazio."
            );
        }
        if(jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32){
            throw new IllegalStateException(
                    "JWT_SECRET deve possuir no mínimo 32 bytes para assinatura HS256."
            );
        }
    }

    @Bean
    public SecretKey jwtSecretKey(){
        return new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder (SecretKey jwtSecretKey){
        return new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey)
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey){
        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
