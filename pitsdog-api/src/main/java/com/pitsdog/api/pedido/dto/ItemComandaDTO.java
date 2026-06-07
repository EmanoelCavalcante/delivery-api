package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.enums.TipoItemPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ItemComandaDTO {
    private Long itemId;

    private TipoItemPedido tipoItem;

    private Long produtoId;

    private Long comboId;

    private String nomeItem;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    private String observacao;

    private List<String> adicionais;
}
