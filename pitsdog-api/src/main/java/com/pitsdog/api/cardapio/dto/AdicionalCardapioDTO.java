package com.pitsdog.api.cardapio.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdicionalCardapioDTO {

    private Long id;
    private String nomeAdicional;
    private BigDecimal preco;
    private Boolean ativo;

}

