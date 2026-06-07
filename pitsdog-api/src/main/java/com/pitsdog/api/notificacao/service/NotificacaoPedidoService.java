package com.pitsdog.api.notificacao.service;

import com.pitsdog.api.pedido.entity.Pedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoPedidoService {

    private final WhatsAppService whatsAppService;
    private final Integer tempoEntregaMinimoMinutos;
    private final Integer tempoEntregaMaximoMinutos;

    public NotificacaoPedidoService(
            WhatsAppService whatsAppService,
            @Value("${TEMPO_ENTREGA_MINIMO_MINUTOS:30}") Integer tempoEntregaMinimoMinutos,
            @Value("${TEMPO_ENTREGA_MAXIMO_MINUTOS:60}") Integer tempoEntregaMaximoMinutos
    ) {
        this.whatsAppService = whatsAppService;
        this.tempoEntregaMinimoMinutos = tempoEntregaMinimoMinutos;
        this.tempoEntregaMaximoMinutos = tempoEntregaMaximoMinutos;
    }

    public void notificarPedidoProntoParaRetirada(Pedido pedido) {
        if (!pedidoPodeReceberNotificacao(pedido)) {
            return;
        }

        if (pedido.getTipoPedido() != TipoPedido.RETIRADA) {
            return;
        }

        String mensagem = montarMensagemPedidoProntoParaRetirada(pedido);

        whatsAppService.enviarMensagem(
                formatarTelefoneParaWhatsApp(pedido.getTelefoneCliente()),
                mensagem
        );
    }

    public void notificarPedidoSaiuParaEntrega(Pedido pedido) {
        if (!pedidoPodeReceberNotificacao(pedido)) {
            return;
        }

        if (pedido.getTipoPedido() != TipoPedido.ENTREGA) {
            return;
        }

        String mensagem = montarMensagemPedidoSaiuParaEntrega(pedido);

        whatsAppService.enviarMensagem(
                formatarTelefoneParaWhatsApp(pedido.getTelefoneCliente()),
                mensagem
        );
    }

    private boolean pedidoPodeReceberNotificacao(Pedido pedido) {
        return pedido != null
                && pedido.getTelefoneCliente() != null
                && !pedido.getTelefoneCliente().isBlank();
    }

    private String montarMensagemPedidoProntoParaRetirada(Pedido pedido) {
        String nomeCliente = resolverNomeCliente(pedido);
        Integer numeroPedido = pedido.getNumeroPedido();

        return """
                Olá, %s! 🍔

                Seu pedido #%s está pronto para retirada.

                Pode vir buscar no Pits Dog. Obrigado pela preferência!
                """.formatted(
                nomeCliente,
                numeroPedido
        );
    }

    private String montarMensagemPedidoSaiuParaEntrega(Pedido pedido) {
        String nomeCliente = resolverNomeCliente(pedido);
        Integer numeroPedido = pedido.getNumeroPedido();

        return """
                Olá, %s! 🛵

                Seu pedido #%s saiu para entrega.

                Tempo estimado de chegada: %d a %d minutos.

                Obrigado pela preferência!
                """.formatted(
                nomeCliente,
                numeroPedido,
                tempoEntregaMinimoMinutos,
                tempoEntregaMaximoMinutos
        );
    }

    private String resolverNomeCliente(Pedido pedido) {
        if (pedido.getNomeCliente() == null || pedido.getNomeCliente().isBlank()) {
            return "cliente";
        }

        return pedido.getNomeCliente();
    }

    private String formatarTelefoneParaWhatsApp(String telefone) {
        String somenteNumeros = telefone.replaceAll("\\D", "");

        if (somenteNumeros.startsWith("55")) {
            return somenteNumeros;
        }

        return "55" + somenteNumeros;
    }
}