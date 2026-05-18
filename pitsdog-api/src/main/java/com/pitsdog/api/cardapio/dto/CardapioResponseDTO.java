package com.pitsdog.api.cardapio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardapioResponseDTO {

    private List<CategoriaCardapioDTO> categorias;
    private List<AdicionalCardapioDTO> adicionais;

}

