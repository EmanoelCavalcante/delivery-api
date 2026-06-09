package com.pitsdog.api.pagamento.dto;

import com.pitsdog.api.pedido.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ConfirmarPagamentoPedidoDTO {

    @NotNull(message = "formaPagamento é obrigatória")
    private FormaPagamento formaPagamento;

    private BigDecimal trocoPara;

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getTrocoPara() {
        return trocoPara;
    }

    public void setTrocoPara(BigDecimal trocoPara) {
        this.trocoPara = trocoPara;
    }
}
