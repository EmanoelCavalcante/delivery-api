package com.pitsdog.api.pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AtualizarQuantidadeItemPedidoDTO {

    @NotNull(message = "quantidade é obrigatória")
    @Min(value = 1, message = "quantidade deve ser maior ou igual a 1")
    private Integer quantidade;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
