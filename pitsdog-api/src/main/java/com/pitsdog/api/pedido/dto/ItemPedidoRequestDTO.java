package com.pitsdog.api.pedido.dto;

import com.pitsdog.api.pedido.entity.TipoItemPedido;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
public class ItemPedidoRequestDTO {

    private Long produtoId;

    private Long comboId;

    @NotNull(message = "tipoItem é obrigatório")
    private TipoItemPedido tipoItem;

    @NotNull(message = "quantidade é obrigatória")
    @Min(value = 1, message = "quantidade deve ser maior ou igual a 1")
    private Integer quantidade;

    private String observacao;

    @Valid
    private List<ItemPedidoAdicionalRequestDTO> adicionais;

    public ItemPedidoRequestDTO() {
    }
}
