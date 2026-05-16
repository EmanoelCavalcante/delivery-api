package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemPedidoAdicionalResponseDTO {

    private Long id;
    private Long adicionalId;
    private String nomeAdicional;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public ItemPedidoAdicionalResponseDTO(){}

    public ItemPedidoAdicionalResponseDTO(Long id,
                                          Long adicionalId,
                                          String nomeAdicional,
                                          Integer quantidade,
                                          BigDecimal precoUnitario,
                                          BigDecimal subtotal) {
        this.id = id;
        this.adicionalId = adicionalId;
        this.nomeAdicional = nomeAdicional;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }
}
