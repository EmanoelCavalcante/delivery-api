package com.pitsdog.api.categoria.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, length = 250)
    private String descricao;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(length = 500, name = "categoria_imagemUrl")
    private String imagemUrl;

    public Categoria(Long id,
                     String nome,
                     boolean ativo,
                     String descricao,
                     Integer ordem,
                     String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.descricao = descricao;
        this.ordem = ordem;
        this.imagemUrl = imagemUrl;
    }

    public Categoria() {
    }

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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}

