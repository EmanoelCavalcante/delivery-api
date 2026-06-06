package com.pitsdog.api.config;

import com.pitsdog.api.config.ratelimit.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(RateLimitFilter rateLimitFilter) {
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/error").permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/debug").authenticated()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/categorias",
                                "/categorias/",
                                "/categorias/**",

                                "/produtos",
                                "/produtos/",
                                "/produtos/**",

                                "/adicionais",
                                "/adicionais/",
                                "/adicionais/**",

                                "/combos",
                                "/combos/",
                                "/combos/**",

                                "/loja/status"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/pedidos",
                                "/pedidos/"
                        ).permitAll()

                        /*
                         * GET /pedidos/** não fica público:
                         * ele expõe telefone, endereço e dados do pedido.
                         */

                        .requestMatchers(HttpMethod.GET, "/health").permitAll()

                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        http.addFilterBefore(
                rateLimitFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");

            if (role == null || role.isBlank()) {
                return List.of();
            }

            if (role.startsWith("ROLE_")) {
                return criarAuthority(role);
            }

            return criarAuthority("ROLE_" + role);
        });

        return authenticationConverter;
    }

    private Collection<GrantedAuthority> criarAuthority(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
