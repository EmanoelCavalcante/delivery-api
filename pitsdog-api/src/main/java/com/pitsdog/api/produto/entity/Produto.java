package com.pitsdog.api.produto.entity;

import com.pitsdog.api.categoria.entity.Categoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 225)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Produto(Long id,
                   String nome,
                   BigDecimal preco,
                   String descricao,
                   boolean ativo,
                   String imageUrl,
                   Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.ativo = ativo;
        this.imageUrl = imageUrl;
        this.categoria = categoria;
    }
}

