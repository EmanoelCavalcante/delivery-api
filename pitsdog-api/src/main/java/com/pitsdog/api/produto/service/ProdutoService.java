package com.pitsdog.api.produto.service;

import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.categoria.repository.CategoriaRepository;
import com.pitsdog.api.produto.dto.ProdutoRequestDTO;
import com.pitsdog.api.produto.dto.ProdutoResponseDTO;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private ProdutoResponseDTO toResponseDTO (Produto produto){
        return new ProdutoResponseDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getDescricao(),
                        produto.getPreco(),
                        produto.getImageUrl(),
                produto.getDisponivel(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome()
                );
    }

    public ProdutoResponseDTO createProduto(ProdutoRequestDTO dto){
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(()->
                        new RuntimeException("Categoria não encontrada"));

        Produto produto = new Produto(
                dto.getNome(),
                dto.getPreco(),
                dto.getDescricao(),
                dto.getImagemUrl(),
                categoria
        );
        Produto saveProduto = produtoRepository.save(produto);

        return toResponseDTO(produto);
    }

    public ProdutoResponseDTO getProdutoById(Long id){
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("Produto não encontrado"));

        return toResponseDTO(produto);
    }

    public List<ProdutoResponseDTO> listAllProdutos(){
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();

    }

    public ProdutoResponseDTO updateProduto(Long id, ProdutoRequestDTO dto){

        System.out.println("Id produto recebido: " + id);
        System.out.println("Id da categoria recebida no PUT: " + dto.getCategoriaId());

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("Produto não encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada"));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setImageUrl(dto.getImagemUrl());
        produto.setDisponivel(dto.getDisponivel());
        produto.setCategoria(categoria);

        Produto updateProduto = produtoRepository.save(produto);

        return toResponseDTO(updateProduto);
    }

    public void deleteProduto (Long id){
        if(!produtoRepository.existsById(id)){
            throw new RuntimeException("Produto não encontrado");
        }

        produtoRepository.deleteById(id);
    }

}

