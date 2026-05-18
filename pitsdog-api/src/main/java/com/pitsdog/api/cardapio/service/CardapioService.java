package com.pitsdog.api.cardapio.service;

import com.pitsdog.api.cardapio.dto.AdicionalCardapioDTO;
import com.pitsdog.api.cardapio.dto.CardapioResponseDTO;
import com.pitsdog.api.cardapio.dto.CategoriaCardapioDTO;
import com.pitsdog.api.cardapio.dto.ProdutoCardapioDTO;
import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.categoria.repository.CategoriaRepository;
import com.pitsdog.api.pedido.entity.Adicional;
import com.pitsdog.api.pedido.repository.AdicionalRepository;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardapioService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final AdicionalRepository adicionalRepository;

    public CardapioService(CategoriaRepository categoriaRepository,
                           ProdutoRepository produtoRepository,
                           AdicionalRepository adicionalRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    private ProdutoCardapioDTO converterProdutoParaCardapioDTO(Produto produto){
        ProdutoCardapioDTO dto = new ProdutoCardapioDTO();


        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setImageUrl(produto.getImagemUrl());
        dto.setAtivo(produto.getAtivo());
        dto.setDisponivel(produto.getAtivo());
        dto.setPermiteAdicionais(produto.getPermiteAdicionais());

        return dto;
    }

    private AdicionalCardapioDTO converterAdicionaisParaCardapioDTO(Adicional adicional){
        AdicionalCardapioDTO dto = new AdicionalCardapioDTO();

        dto.setId(adicional.getId());
        dto.setNomeAdicional(adicional.getNomeAdicional());
        dto.setPreco(adicional.getPreco());
        dto.setAtivo(adicional.getAtivo());

        return dto;
    }


    private CategoriaCardapioDTO converterCategoriaParaCardapioDTO(Categoria categoria){
        CategoriaCardapioDTO dto = new CategoriaCardapioDTO();

        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        dto.setImageUrl(categoria.getImagemUrl());
        dto.setOrdem(categoria.getOrdem());

        List<Produto> produtos = produtoRepository
                .findByCategoriaIdAndAtivoTrueOrderByNomeAsc(categoria.getId());

        List<ProdutoCardapioDTO> produtosDTO = produtos.stream()
                .map(this::converterProdutoParaCardapioDTO)
                .toList();

        dto.setProdutos(produtosDTO);

        return dto;
    }




    public CardapioResponseDTO listCardapio(){
        List<Categoria> categorias = categoriaRepository.findByAtivoTrueOrderByOrdemAsc();
        List<Adicional> adicionais = adicionalRepository.findByAtivoTrue();

        List<CategoriaCardapioDTO> categoriasDTO = categorias.stream()
                .map(this::converterCategoriaParaCardapioDTO)
                .toList();

        List<AdicionalCardapioDTO> adicionaisDTO = adicionais.stream()
                .map(this::converterAdicionaisParaCardapioDTO)
                .toList();

        CardapioResponseDTO response = new CardapioResponseDTO();
        response.setCategorias(categoriasDTO);
        response.setAdicionais(adicionaisDTO);

        return  response;
    }
}
