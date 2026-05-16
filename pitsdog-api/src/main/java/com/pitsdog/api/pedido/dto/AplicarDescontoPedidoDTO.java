package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AplicarDescontoPedidoDTO {

    private BigDecimal descontoManualPercentual;

    private BigDecimal descontoManualValor;

    public AplicarDescontoPedidoDTO() {
    }
}
