package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.FormaPagamento;
import com.pitsdog.api.pedido.entity.OrigemPedido;
import com.pitsdog.api.pedido.entity.StatusPedido;
import com.pitsdog.api.pedido.entity.TipoPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class PedidoDTO {

        private Long id;

        private Integer numeroPedido;

        private TipoPedido tipoPedido;

        private Integer numeroMesa;

        private String nomeCliente;

        private String telefoneCliente;

        private String bairroEntrega;

        private String ruaEntrega;

        private Integer numeroCasa;

        private String complemento;

        private OrigemPedido origemPedido;

        private String observacao;

        private StatusPedido status;

        private LocalDateTime momentoPedido;

        private LocalDateTime previsaoRetirada;

        private BigDecimal subtotal;

        private BigDecimal descontoManualPercentual;

        private BigDecimal descontoManualValor;

        private BigDecimal descontoFidelidadePercentual;

        private BigDecimal descontoFidelidadeValor;

        private BigDecimal taxaEntrega;

        private BigDecimal total;

        private FormaPagamento formaPagamento;

        private List<ItemPedidoDTO> itens;
}
