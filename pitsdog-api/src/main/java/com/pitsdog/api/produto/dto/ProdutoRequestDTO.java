package com.pitsdog.api.produto.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ProdutoRequestDTO {

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemUrl;
    private Boolean disponivel;
    private Long categoriaId;

}
