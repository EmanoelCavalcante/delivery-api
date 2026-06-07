package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.enums.FormaPagamento;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PedidoResumoResponseDTO {

    private Long id;

    private Integer numeroPedido;

    private TipoPedido tipoPedido;

    private Integer numeroMesa;

    private String nomeCliente;

    private String telefoneCliente;

    private StatusPedido status;

    private LocalDateTime momentoPedido;

    private BigDecimal total;

    private FormaPagamento formaPagamento;
}
