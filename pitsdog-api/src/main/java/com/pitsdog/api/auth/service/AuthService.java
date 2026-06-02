package com.pitsdog.api.auth.service;

import com.pitsdog.api.auth.dto.LoginRequestDTO;
import com.pitsdog.api.auth.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPasswordHash;
    private final long jwtExpirationSeconds;

    public AuthService(
            JwtEncoder jwtEncoder,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_PASSWORD_HASH}")String adminPasswordHash,
            @Value("${ADMIN_EMAIL}")String adminEmail,
            @Value("${JWT_EXPIRATION_SECONDS:3600}")long jwtExpirationSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPasswordHash = adminPasswordHash;
        this.jwtExpirationSeconds = jwtExpirationSeconds;

        validarConfiguracoes();
    }

    private void validarConfiguracoes(){
        if(adminEmail == null || adminEmail.isBlank()){
            throw new IllegalStateException("ADMIN_EMAIL não pode estar vazio");
        }

        if(adminPasswordHash == null || adminPasswordHash.isBlank()){
            throw new IllegalStateException("ADMIN_PASSWORD_HASH não pode estar vazia");
        }

        if(jwtExpirationSeconds <= 0){
            throw new IllegalStateException("JWT_EXPIRATION_SECONDS deve ser maior que zero");
        }
    }

    public LoginResponseDTO login(LoginRequestDTO dto){
        String emailInformado = dto.getEmail() == null ? "" : dto.getEmail().trim();

        String senhaInformada = dto.getSenha() == null ? "" : dto.getSenha();

        boolean emailValido = adminEmail.equalsIgnoreCase(emailInformado);
        boolean senhaValida = passwordEncoder.matches(senhaInformada, adminPasswordHash);

        if(!emailValido || !senhaValida){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciais inválidas"
            );
        }

        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(jwtExpirationSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("pitsdog-api")
                .issuedAt(agora)
                .expiresAt(expiracao)
                .subject(adminEmail)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponseDTO(
                token,
                "Bearer",
                jwtExpirationSeconds
        );
    }
}
