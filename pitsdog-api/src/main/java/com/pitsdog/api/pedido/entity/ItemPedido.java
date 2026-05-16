package com.pitsdog.api.pedido.entity;


import com.pitsdog.api.produto.entity.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itens_pedido")
@Getter
@Setter
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoItemPedido tipoItem;

    private String nomeProduto;

    private String observacao;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @OneToMany(mappedBy = "itemPedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedidoAdicional> adicional = new ArrayList<>();

    public ItemPedido() {
    }
}

