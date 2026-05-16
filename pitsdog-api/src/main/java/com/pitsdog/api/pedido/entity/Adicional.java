package com.pitsdog.api.pedido.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "adicional")
@Getter
@Setter
public class Adicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAdicional;

    private BigDecimal preco;

    private Boolean ativo = true;

    public Adicional(){}
}
