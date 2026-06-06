package com.pitsdog.api.categoria.service;

import com.pitsdog.api.categoria.dto.CategoriaRequestDTO;
import com.pitsdog.api.categoria.dto.CategoriaResponseDTO;
import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.categoria.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria){
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getImagemUrl(),
                categoria.getOrdem(),
                categoria.isAtivo()
        );
    }

    public List<CategoriaResponseDTO> listCategorias(){
        return categoriaRepository.findByAtivoTrueOrderByOrdemAsc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<CategoriaResponseDTO> listAllCategorias(){
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CategoriaResponseDTO createCategoria (CategoriaRequestDTO dto){
        Categoria categoria = new Categoria();

        categoria.setNome(dto.nome());
        categoria.setAtivo(dto.ativo());
        categoria.setDescricao(dto.descricao());
        categoria.setOrdem(dto.ordem());
        categoria.setImagemUrl(dto.imagemUrl());

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return toResponseDTO(categoriaSalva);
    }

    public CategoriaResponseDTO updateCategoria(
            Long id,
            CategoriaRequestDTO dto){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("Categoria não encontrada"));

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());
        categoria.setImagemUrl(dto.imagemUrl());
        categoria.setOrdem(dto.ordem());
        categoria.setAtivo(dto.ativo());

        Categoria categoriaAtualizada =
                categoriaRepository.save(categoria);

        return toResponseDTO(categoriaAtualizada);
    }

    public CategoriaResponseDTO updateStatus(
            Long id,
            Boolean ativo
    ){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("Categoria não encontrada"));

        categoria.setAtivo(ativo);

        Categoria categoriaAtualizada =
                categoriaRepository.save(categoria);

        return toResponseDTO(categoriaAtualizada);
    }

    public void deleteCategoria(Long id){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("Categoria não encontrada"));

        categoriaRepository.delete(categoria);
    }
}

