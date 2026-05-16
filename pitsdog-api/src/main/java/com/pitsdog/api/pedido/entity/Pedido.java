package com.pitsdog.api.pedido.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroPedido;

    private Integer numeroMesa;

    private LocalDateTime previsaoRetirada;

    private String nomeCliente;

    private String telefoneCliente;

    private String bairroEntrega;

    private String ruaEntrega;

    private String complmeneto;

    private Integer numeroCasa;

    private BigDecimal subtotal;

    private BigDecimal taxaEntrega;

    private BigDecimal total;

    private BigDecimal descontoManualValor;

    private BigDecimal descontoFidelidadeValor;

    private BigDecimal descontoManualPercentual;

    private BigDecimal descontoFidelidadePercentual;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Enumerated(EnumType.STRING)
    private TipoPedido tipoPedido;

    @ManyToOne
    @JoinColumn(name = "combo_id")
    private Combo combo;

    private LocalDate momentoPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @PrePersist
    public void prePersist() {
        this.momentoPedido = LocalDate.now();

        if (this.status == null) {
            this.status = StatusPedido.AGUARDANDO_APROVACAO;
        }
    }

    public Pedido() {
    }

    public void setComplemento(String complemento){
        this.complmeneto = complemento;
    }

    public void setMomentoPedido(LocalDateTime now) {

    }
}