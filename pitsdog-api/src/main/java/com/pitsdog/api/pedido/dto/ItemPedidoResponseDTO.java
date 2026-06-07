package com.pitsdog.api.pedido.dto;


import com.pitsdog.api.pedido.enums.TipoItemPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ItemPedidoResponseDTO {
    private Long id;

    private TipoItemPedido tipoItem;

    private Long produtoId;
    private Long comboId;

    private String nomeProduto;
    private String nomeCombo;

    private String observacao;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    private List<ItemPedidoAdicionalDTO> adicionais;

    public ItemPedidoResponseDTO() {
    }
}
