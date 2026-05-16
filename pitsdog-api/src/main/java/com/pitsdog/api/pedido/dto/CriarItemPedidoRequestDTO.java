package com.pitsdog.api.pedido.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CriarItemPedidoRequestDTO {

    private Long produtoId;
    private Integer quantidade;
    private String observacao;

    private List<CriarItemAdicionalRequestDTO> complementos;

    public CriarItemPedidoRequestDTO() {
    }
}
