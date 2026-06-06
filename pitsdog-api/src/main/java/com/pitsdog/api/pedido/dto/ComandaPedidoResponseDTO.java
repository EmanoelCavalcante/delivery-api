package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.FormaPagamento;
import com.pitsdog.api.pedido.entity.StatusPedido;
import com.pitsdog.api.pedido.entity.TipoPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ComandaPedidoResponseDTO {

    private Long pedidoId;

    private Integer numeroPedido;

    private LocalDateTime momentoPedido;

    private StatusPedido status;

    private TipoPedido tipoPedido;

    private String nomeCliente;

    private String telefoneCliente;

    private Integer numeroMesa;

    private String bairroEntrega;

    private String ruaEntrega;

    private Integer numeroCasa;

    private String complemento;

    private String observacao;

    private FormaPagamento formaPagamento;

    private BigDecimal subtotal;

    private BigDecimal taxaEntrega;

    private BigDecimal descontoManualValor;

    private BigDecimal descontoFidelidadeValor;

    private BigDecimal total;

    private List<ItemComandaDTO> itens;

    private String textoImpressao;
}