package com.pitsdog.api.loja.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "loja_configuracao")
public class LojaConfiguracao {

    @Id
    private long id = 1L;

    @Column(nullable = false)
    private Boolean aceitaEntrega = true;

    @Column(nullable = false)
    private Boolean aceitaRetirada = true;

    @Column(nullable = false)
    private Boolean aceitaMesa = true;

    @Column(columnDefinition = "TEXT")
    private String mensagemFechamento;

    private LocalDateTime atualizadoEm;


    @PrePersist
    @PreUpdate
    public void atualizarMomento(){
        this.atualizadoEm = LocalDateTime.now();
    }

    public LojaConfiguracao() {
    }
}
