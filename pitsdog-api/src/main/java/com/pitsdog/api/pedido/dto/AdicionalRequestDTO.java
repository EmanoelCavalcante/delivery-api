package com.pitsdog.api.pedido.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdicionalRequestDTO {

    private String nomeAdicional;

    private BigDecimal preco;

    private Boolean ativo;
}
