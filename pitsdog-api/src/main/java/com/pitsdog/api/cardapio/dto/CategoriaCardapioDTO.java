package com.pitsdog.api.cardapio.dto;


import java.util.List;


public class CategoriaCardapioDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String imageUrl;
    private Integer ordem;
    private List<ProdutoCardapioDTO> produtos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public List<ProdutoCardapioDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoCardapioDTO> produtos) {
        this.produtos = produtos;
    }
}

