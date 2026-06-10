package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.enums.FormaPagamento;
import com.pitsdog.api.pedido.enums.OrigemPedido;
import com.pitsdog.api.pedido.enums.TipoPedido;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CriarPedidoRequestDTO {


    @NotNull(message = "tipoPedido é obrigatório")
    private TipoPedido tipoPedido;

    private Integer numeroMesa;

    private String nomeCliente;
    @Pattern(
            regexp = "^$|^[0-9]{10,11}$",
            message = "Telefone deve conter 10 ou 11 dígitos"
    )
    private String telefoneCliente;

    private String bairroEntrega;

    private String ruaEntrega;

    private Integer numeroCasa;

    private String complemento;

    private OrigemPedido origemPedido;

    private String observacao;

    private LocalDateTime previsaoRetirada;

    @NotNull(message = "formaPagamento é obrigatório")
    private FormaPagamento formaPagamento;

    private BigDecimal taxaEntrega;

    private BigDecimal descontoManualPercentual;

    private BigDecimal descontoManualValor;

    @NotEmpty(message = "itens é obrigatório e não pode ser vazio")
    @Valid
    private List<ItemPedidoRequestDTO> itens;



    public CriarPedidoRequestDTO() {
    }

}
