package com.pitsdog.api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    private final Cache<String, Bucket> consultaPublicaBuckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    private Bucket criarBucketLogin(){
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillGreedy(5, Duration.ofMinutes(10)))
                .build();
    }


    private Bucket criarBucketConsultaPublica(){
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(300)
                        .refillGreedy(5,Duration.ofMinutes(10)))
                .build();
    }

    private String extrairIpDoCliente(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if(forwardedFor != null && !forwardedFor.isBlank()){
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String metodo = request.getMethod();
        String rota = request.getRequestURI();

        boolean login = HttpMethod.POST.matches(metodo)
                && "/auth/login".equals(rota);

        boolean statusLoja = HttpMethod.GET.matches(metodo)
                && "/loja/status".equals(rota);

        boolean cardapioPublico = HttpMethod.GET.matches(metodo)
                && (
                rota.startsWith("/categorias")
                        || rota.startsWith("/produtos")
                        || rota.startsWith("/adicionais")
                        || rota.startsWith("/combos")
        );

        return !(login || statusLoja || cardapioPublico);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )   throws ServletException, IOException{

        String ip = extrairIpDoCliente(request);
        String rota = request.getRequestURI();
        String metodo = request.getMethod();

        Bucket bucket;

        if(HttpMethod.POST.matches(metodo) && "/auth/login".equals(rota)){
            bucket = loginBuckets.get(ip, chave -> criarBucketLogin());
        } else{
            bucket = consultaPublicaBuckets.get(ip, chave -> criarBucketConsultaPublica());
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);


        if(!probe.isConsumed()){
            long segundosParaTentarNovamente =
                    Math.max(1, Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds());

            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(segundosParaTentarNovamente));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(
                    """
                        {
                          "status": 429,
                          "erro": "Limite de requisições excedido."
                          "mensagem": "Tente novamente em instantes."
                        }
                        """
            );

            return;
        }

        response.setHeader(
                "X-Rate-Limit-Remaining",
                String.valueOf(probe.getRemainingTokens())
        );

        filterChain.doFilter(request, response);
    }
}


