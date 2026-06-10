package com.pitsdog.api.pedido.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.pitsdog.api.pedido.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public class AtualizarStatusPedidoDTO {

    @NotNull(message = "status é obrigatório")
    @JsonAlias("novoStatus")
    private StatusPedido status;


    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

}

