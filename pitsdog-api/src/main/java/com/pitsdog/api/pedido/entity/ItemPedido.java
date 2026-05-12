package com.pitsdog.api.pedido.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_pedidos")
@Getter
@Setter
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeProduto;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public ItemPedido() {
    }

    public BigDecimal getSubtotal() {
        return this.subTotal;
    }
}

