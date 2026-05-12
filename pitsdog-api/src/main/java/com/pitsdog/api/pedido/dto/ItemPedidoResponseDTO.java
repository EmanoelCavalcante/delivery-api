package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.StatusPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemPedidoResponseDTO {
    private Long id;

    private String nomeProduto;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    public ItemPedidoResponseDTO() {
    }
}
