package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemPedidoAdicionalDTO {

    private Long id;

    private Long adicionalId;

    private String nomeAdicional;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;
}