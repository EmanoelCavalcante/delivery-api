package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.enums.StatusPedido;

public class AtualizarStatusPedidoDTO {

    private StatusPedido status;


    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

}

