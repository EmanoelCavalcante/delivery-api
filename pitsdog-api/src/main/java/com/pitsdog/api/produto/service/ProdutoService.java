package com.pitsdog.api.produto.service;

import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.categoria.repository.CategoriaRepository;
import com.pitsdog.api.produto.dto.ProdutoRequestDTO;
import com.pitsdog.api.produto.dto.ProdutoResponseDTO;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private Produto buscarprodutoEntityById(Long id){
        return produtoRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Produto não encontrado"));
    }

    private Categoria buscarCategoriaEntityById(Long categoriaId){
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    private void validarProdutoDTO(ProdutoRequestDTO dto){
        if(dto.getNome() == null || dto.getNome().isBlank()){
            throw new RuntimeException("Nome do produto é obrigatório");
        }
        if(dto.getPreco() == null || dto.getPreco().compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Preço do produto deve ser maior que zero");
        }
        if(dto.getCategoriaId() == null){
            throw new RuntimeException("CategoriaId não pode ser null");
        }
    }

    private ProdutoResponseDTO toResponseDTO (Produto produto){
        return new ProdutoResponseDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getDescricao(),
                        produto.getPreco(),
                        produto.getImagemUrl(),
                produto.getAtivo(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome()
                );
    }

    private void aplicarPermiteAdicionaisSePresente(Produto produto, ProdutoRequestDTO dto){
        try{
            Method m = dto.getClass().getMethod("getPermiteAdicionais");
            Object v = m.invoke(dto);
            if(v instanceof Boolean){
                Boolean b = (Boolean) v;
                if(b != null){
                    produto.setPermiteAdicionais(b);
                }
            }
        }catch (ReflectiveOperationException ignored){
        }
    }

    public ProdutoResponseDTO createProduto(ProdutoRequestDTO dto){

        if(dto.getCategoriaId() == null){
            throw new RuntimeException("CategoriaId não pode ser null");
        }

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

        aplicarPermiteAdicionaisSePresente(produto, dto);

        if(dto.getAtivo() != null){
            produto.setAtivo(dto.getAtivo());
        }
        else{
            produto.setAtivo(true);
        }

        Produto saveProduto = produtoRepository.save(produto);

        return toResponseDTO(saveProduto);
    }

    public ProdutoResponseDTO getProdutoById(Long id){

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("Produto não encontrado"));

        return toResponseDTO(produto);
    }

    public List<ProdutoResponseDTO> listProdutosAtivos(){
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();

    }
    public List<ProdutoResponseDTO> listAllProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO updateProduto(Long id, ProdutoRequestDTO dto){

        Produto produto = buscarprodutoEntityById(id);

        Categoria categoria = buscarCategoriaEntityById(dto.getCategoriaId());

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setImagemUrl(dto.getImagemUrl());
        produto.setAtivo(dto.getAtivo());
        produto.setCategoria(categoria);

        aplicarPermiteAdicionaisSePresente(produto, dto);

        if(dto.getAtivo() == null){
            produto.setAtivo(dto.getAtivo());
        }

        Produto updateProduto = produtoRepository.save(produto);

        return toResponseDTO(updateProduto);
    }

    public ProdutoResponseDTO ativarProduto(Long id){
        Produto produto = buscarprodutoEntityById(id);

        produto.setAtivo(true);

        Produto produtoAtualizado = produtoRepository.save(produto);

        return  toResponseDTO(produtoAtualizado);
    }

    public ProdutoResponseDTO desativarProduto(Long id){
        Produto produto = buscarprodutoEntityById(id);

        produto.setAtivo(false);

        Produto produtoAtualizado = produtoRepository.save(produto);

        return  toResponseDTO(produtoAtualizado);
    }

    public ProdutoResponseDTO updateStatus(
            Long id,
            Boolean ativo
    ){
        if(ativo == null){
            throw new RuntimeException("Status não pode ser null");
        }
        Produto produto = buscarprodutoEntityById(id);
        produto.setAtivo(ativo);


        Produto produtoAtualizado = produtoRepository.save(produto);
        return toResponseDTO(produtoAtualizado);
    }

    public void deleteProduto (Long id){
        if(!produtoRepository.existsById(id)){
            throw new RuntimeException("Produto não encontrado");
        }

        produtoRepository.deleteById(id);
    }

}
