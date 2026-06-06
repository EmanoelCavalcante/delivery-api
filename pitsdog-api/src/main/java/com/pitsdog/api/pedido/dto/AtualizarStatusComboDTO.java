package com.pitsdog.api.pedido.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarStatusComboDTO {

    @NotNull(message = "O status ativo é obrigatório")
    private Boolean ativo;
}
