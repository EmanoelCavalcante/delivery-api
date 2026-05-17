package com.pitsdog.api.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AdicionalResponseDTO {

    private Long id;

    private String nomedAicional;

    private BigDecimal preco;

    private Boolean ativo;
}
