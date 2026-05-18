package com.pitsdog.api.cardapio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoriaCardapioDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String imageUrl;
    private Integer ordem;
    private List<ProdutoCardapioDTO> produtos;

}

