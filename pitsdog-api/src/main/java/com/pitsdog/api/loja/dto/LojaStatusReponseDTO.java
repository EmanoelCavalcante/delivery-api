package com.pitsdog.api.loja.dto;


import com.pitsdog.api.loja.entity.EstadoOperacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LojaStatusReponseDTO {

    private EstadoOperacao estadoOperacao;

    private Boolean aberta;

    private Boolean aceitaEntrega;

    private Boolean aceitaRetirada;

    private Boolean aceitaMesa;

    private String mensagem;

    private LocalDateTime atualizadoEm;
}
