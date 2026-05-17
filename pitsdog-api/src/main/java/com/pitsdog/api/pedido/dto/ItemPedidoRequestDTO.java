package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.TipoItemPedido;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemPedidoRequestDTO {

    private Long produtoId;

    private Long comboId;

    private TipoItemPedido tipoItem;

    private Integer quantidade;

    private String observacao;

    private List<ItemPedidoAdicionalRequestDTO> adicionais;

    public ItemPedidoRequestDTO() {
    }
}

