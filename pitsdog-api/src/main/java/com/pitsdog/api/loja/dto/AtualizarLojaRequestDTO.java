package com.pitsdog.api.loja.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarLojaRequestDTO {

    @NotNull
    private Boolean aceitaEntrega;

    @NotNull
    private Boolean aceitaRetirada;

    @NotNull
    private Boolean aceitaMesa;

    private String mensagemFechamento;
}
