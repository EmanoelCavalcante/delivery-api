package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.StatusPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class PedidoResponseDTO {
    private Long id;

    private String nomeCliente;

    private String telefoneCliente;

    private String bairroEntrega;

    private String ruaEntrega;

    private String complemento;

    private Integer numeroCasa;

    private String formaPagamento;

    private BigDecimal subtotal;

    private BigDecimal taxaEntrega;

    private BigDecimal total;

    private StatusPedido status;

    private LocalDateTime criadoEm;

    private List<ItemPedidoResponseDTO> itens;

    public PedidoResponseDTO() {
    }
}