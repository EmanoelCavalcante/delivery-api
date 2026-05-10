package com.pitsdog.api.categoria.dto;

public record CategoriaRequestDTO(
        String nome,
        String descricao,
        String imagem,
        Integer ordem,
        Boolean ativo
) {
}
