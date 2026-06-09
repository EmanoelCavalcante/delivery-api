package com.pitsdog.api.pedido.entity;

import com.pitsdog.api.pagamento.enums.StatusPagamento;
import com.pitsdog.api.pedido.enums.FormaPagamento;
import com.pitsdog.api.pedido.enums.OrigemPedido;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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

    // Mantemos o nome do atributo interno por compatibilidade com o schema atual.
    //    // Externamente, usamos getter/setter "Complemento" para alinhar com o DTO/JSON.
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
    @Column(nullable = false)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(nullable = false)
    private Boolean pagamentoConfirmado = false;

    private LocalDateTime momentoPagamentoConfirmado;

    private BigDecimal trocoPara;

    private BigDecimal valorTroco;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Enumerated(EnumType.STRING)
    private TipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_pedido", nullable = false)
    private OrigemPedido origemPedido;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "combo_id")
    private Combo combo;

    private LocalDateTime momentoPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @PrePersist
    public void prePersist() {
        if (this.momentoPedido == null) {
            this.momentoPedido = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = StatusPedido.AGUARDANDO_APROVACAO;
        }

        if(this.origemPedido == null){
            this.origemPedido = OrigemPedido.SITE;
        }
    }

    public Pedido() {
    }

    public void setComplemento(String complemento){
        this.complmeneto = complemento;
    }

    public String getComplemento() {
        return this.complmeneto;
    }
}
