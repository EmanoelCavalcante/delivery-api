package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.enums.FormaPagamento;

public class AtualizarPagamentoPedidoDTO {

    private FormaPagamento formaPagamento;

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public AtualizarPagamentoPedidoDTO() {
    }
}
