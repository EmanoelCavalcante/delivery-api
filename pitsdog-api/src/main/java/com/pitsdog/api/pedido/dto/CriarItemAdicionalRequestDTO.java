package com.pitsdog.api.pedido.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarItemAdicionalRequestDTO {

    private Long complementoId;
    private Integer quantidade;

    public CriarItemAdicionalRequestDTO(){}
}
