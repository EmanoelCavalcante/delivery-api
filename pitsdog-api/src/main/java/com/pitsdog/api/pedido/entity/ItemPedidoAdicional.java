package com.pitsdog.api.pedido.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido_adicional")
@Getter
@Setter
public class ItemPedidoAdicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_pedido_id")
    private ItemPedido itemPedido;

    @ManyToOne
    @JoinColumn(name = "adicional_id")
    private Adicional adicional;

    private String nomeAdicional;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    public ItemPedidoAdicional(){}
}
