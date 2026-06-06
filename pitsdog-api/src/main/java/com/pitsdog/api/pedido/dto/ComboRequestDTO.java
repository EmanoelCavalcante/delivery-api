package com.pitsdog.api.pedido.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ComboRequestDTO {

    @NotBlank(message = "Nome do combo é obrigatório")
    private String nome;

    private String descricao;

    @NotBlank(message = "Preço do combo é obrigatório")
    @DecimalMin(value = "0.00", message = "Preço do combo não pode ser negativo")
    private BigDecimal preco;

    private String imagemUrl;

    private Boolean ativo;
}
