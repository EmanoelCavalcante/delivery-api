package com.pitsdog.api.pedido.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCliente;

    private String telefoneCliente;

    private String bairroEntrega;

    private String ruaEntrega;

    private String complmeneto;

    private Integer numeroCasa;

    private String formaPagamento;

    private BigDecimal subtotal;

    private BigDecimal taxaEntrega;

    private BigDecimal total;


    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private LocalDate criadoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDate.now();

        if (this.status == null) {
            this.status = StatusPedido.AGUARDANDO_APROVACAO;
        }
    }

    public Pedido() {
    }


}