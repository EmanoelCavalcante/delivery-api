package com.pitsdog.api.categoria.dto;

public record CategoriaRequestDTO(
        String nome,
        String descricao,
        String imagemUrl,
        Integer ordem,
        Boolean ativo
) {
}
