package com.pitsdog.api.cardapio.dto;

import java.math.BigDecimal;

public class ProdutoCardapioDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imageUrl;
    private Boolean ativo;
    private Boolean disponivel;
    private Boolean permiteAdicionais;
    private String tag;
    private Boolean destaque;

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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    public Boolean getPermiteAdicionais() {
        return permiteAdicionais;
    }

    public void setPermiteAdicionais(Boolean permiteAdicionais) {
        this.permiteAdicionais = permiteAdicionais;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getDestaque() {
        return destaque;
    }

    public void setDestaque(Boolean destaque) {
        this.destaque = destaque;
    }
}

