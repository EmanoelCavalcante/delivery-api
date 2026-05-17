package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ItemPedidoAdicionalRequestDTO {

    @NotNull(message = "adicionalId é obrigatório")
    private Long adicionalId;

    @NotNull(message = "quantidade é obrigatória")
    @Min(value = 1, message = "quantidade deve ser maior ou igual a 1")
    private Integer quantidade;

    public ItemPedidoAdicionalRequestDTO(){
    }
}
