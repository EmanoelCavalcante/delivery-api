package com.pitsdog.api.produto.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemUrl;
    private Boolean ativo;
    private Long categoriaId;
    private String categoriaNome;

    public ProdutoResponseDTO(Long id,
                              String nome,
                              String descricao,
                              BigDecimal preco,
                              String imagemUrl,
                              Boolean ativo,
                              Long categoriaId,
                              String categoriaNome) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
        this.ativo = ativo;
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
    }
}