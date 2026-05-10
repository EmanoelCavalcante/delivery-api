package com.pitsdog.api.categoria.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Getter
@Setter
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

    @Column(length = 500)
    private String imagem;

    public Categoria(Long id,
                     String nome,
                     boolean ativo,
                     String descricao,
                     Integer ordem,
                     String imagem) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.descricao = descricao;
        this.ordem = ordem;
        this.imagem = imagem;
    }

    public Categoria() {
    }
}

