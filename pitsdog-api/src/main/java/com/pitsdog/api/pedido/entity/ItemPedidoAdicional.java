package com.pitsdog.api.pedido.entity;

import jakarta.persistence.*;



import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido_adicional")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPedido getItemPedido() {
        return itemPedido;
    }

    public void setItemPedido(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
    }

    public Adicional getAdicional() {
        return adicional;
    }

    public void setAdicional(Adicional adicional) {
        this.adicional = adicional;
    }

    public String getNomeAdicional() {
        return nomeAdicional;
    }

    public void setNomeAdicional(String nomeAdicional) {
        this.nomeAdicional = nomeAdicional;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
