package com.pitsdog.api.pedido.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ComboResponseDTO {
    private Long id;

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private String imagemUrl;

    private Boolean ativo;

    public ComboResponseDTO() {
    }

    public ComboResponseDTO(Long id,
                            String nome,
                            String descricao,
                            BigDecimal preco,
                            String imagemUrl,
                            Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
        this.ativo = ativo;
    }
}
