package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PedidoRequestDTO {

    private String nomeCliente;

    private String telefoneCliente;

    private String enderecoEntrega;

    private String formaPagamento;

    private BigDecimal taxaEntrega;

    private List<ItemPedidoRequestDTO> itens;

    public PedidoRequestDTO() {
    }


}
