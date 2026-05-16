package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.FormaPagamento;
import com.pitsdog.api.pedido.entity.TipoPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CriarPedidoRequestDTO {


    private TipoPedido tipoPedido;

    private Integer numeroMesa;

    private String nomeCliente;

    private String telefoneCliente;

    private String bairroEntrega;

    private String ruaEntrega;

    private Integer numeroCasa;

    private String complemento;

    private LocalDateTime previsaoRetirada;

    private FormaPagamento formaPagamento;

    private BigDecimal taxaEntrega;

    private BigDecimal descontoManualPercentual;

    private BigDecimal descontoManualValor;

    private List<ItemPedidoRequestDTO> itens;



    public CriarPedidoRequestDTO() {
    }

}

