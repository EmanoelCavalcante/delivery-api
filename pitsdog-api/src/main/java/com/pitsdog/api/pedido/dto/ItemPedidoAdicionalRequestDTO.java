package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemPedidoAdicionalRequestDTO {

    private Long adicionalId;

    private Integer quantidade;

    public ItemPedidoAdicionalRequestDTO(){
    }
}
