package com.pitsdog.api.cardapio.dto;



import java.util.List;


public class CardapioResponseDTO {

    private List<CategoriaCardapioDTO> categorias;
    private List<AdicionalCardapioDTO> adicionais;


    public List<CategoriaCardapioDTO> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaCardapioDTO> categorias) {
        this.categorias = categorias;
    }

    public List<AdicionalCardapioDTO> getAdicionais() {
        return adicionais;
    }

    public void setAdicionais(List<AdicionalCardapioDTO> adicionais) {
        this.adicionais = adicionais;
    }
}

