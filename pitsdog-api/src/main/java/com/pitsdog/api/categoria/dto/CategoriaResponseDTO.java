package com.pitsdog.api.categoria.dto;


public record CategoriaResponseDTO(
        Long id,
        String nome,
        String descricao,
        String imagemUrl,
        Integer ordem,
        boolean ativo
) {
}

