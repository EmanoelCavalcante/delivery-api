package com.pitsdog.api.config.ratelimit;

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

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private final RateLimitService rateLimitService;
    private final ClientIpResolver clientIpResolver;

    public RateLimitFilter(
            RateLimitService rateLimitService,
            ClientIpResolver clientIpResolver
    ) {
        this.rateLimitService = rateLimitService;
        this.clientIpResolver = clientIpResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return identificarTipo(request) == null;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        RateLimitTipo tipo = identificarTipo(request);

        /*
         * Proteção adicional. Na prática, rotas não limitadas
         * já são barradas em shouldNotFilter().
         */
        if (tipo == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = clientIpResolver.resolver(request);

        ConsumptionProbe probe = rateLimitService.consumir(tipo, ip);

        if (!probe.isConsumed()) {
            responderLimiteExcedido(response, probe);
            return;
        }

        response.setHeader(
                "X-Rate-Limit-Remaining",
                String.valueOf(probe.getRemainingTokens())
        );

        filterChain.doFilter(request, response);
    }

    private RateLimitTipo identificarTipo(HttpServletRequest request) {
        String metodo = request.getMethod();
        String rota = request.getRequestURI();

        boolean login = HttpMethod.POST.matches(metodo)
                && "/auth/login".equals(rota);

        if (login) {
            return RateLimitTipo.LOGIN;
        }

        boolean statusLoja = HttpMethod.GET.matches(metodo)
                && "/loja/status".equals(rota);

        boolean cardapioPublico = HttpMethod.GET.matches(metodo)
                && (
                rota.startsWith("/categorias")
                        || rota.startsWith("/produtos")
                        || rota.startsWith("/adicionais")
                        || rota.startsWith("/combos")
        );

        if (statusLoja || cardapioPublico) {
            return RateLimitTipo.CONSULTA_PUBLICA;
        }

        /*
         * POST /pedidos não entra aqui:
         * pedidos reais não podem ser bloqueados pelo rate limit.
         */
        return null;
    }

    private void responderLimiteExcedido(
            HttpServletResponse response,
            ConsumptionProbe probe
    ) throws IOException {

        long segundosParaTentarNovamente = Math.max(
                1,
                Duration.ofNanos(
                        probe.getNanosToWaitForRefill()
                ).toSeconds()
        );

        response.setStatus(HTTP_TOO_MANY_REQUESTS);
        response.setHeader(
                "Retry-After",
                String.valueOf(segundosParaTentarNovamente)
        );
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                """
                {
                  "status": 429,
                  "erro": "Limite de requisicoes excedido.",
                  "mensagem": "Tente novamente em instantes."
                }
                """
        );
    }
}