package com.pitsdog.api.categoria.service;

import com.pitsdog.api.categoria.dto.CategoriaRequestDTO;
import com.pitsdog.api.categoria.dto.CategoriaResponseDTO;
import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.categoria.repository.CategoriaRepository;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.upload.service.SupabaseStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

    private static final String PASTA_CATEGORIAS = "categorias";

    private static final List<StatusPedido> STATUS_FINAIS = List.of(
            StatusPedido.FINALIZADO,
            StatusPedido.CANCELADO
    );

    private final CategoriaRepository categoriaRepository;
    private final SupabaseStorageService supabaseStorageService;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            SupabaseStorageService supabaseStorageService
    ) {
        this.categoriaRepository = categoriaRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getImagemUrl(),
                categoria.getOrdem(),
                categoria.isAtivo()
        );
    }

    private Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada"
                ));
    }

    private void validarCategoriaSemPedidosEmAndamento(Long categoriaId) {
        if (categoriaRepository.existsVinculadaAPedidosEmAndamento(categoriaId, STATUS_FINAIS)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível desativar categoria pois está vinculada a pedidos em andamento."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listCategorias() {
        return categoriaRepository.findByAtivoTrueOrderByOrdemAsc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listAllCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Mantém compatibilidade com o fluxo antigo JSON.
     */
    @Transactional
    public CategoriaResponseDTO createCategoria(CategoriaRequestDTO dto) {
        return createCategoria(dto, null);
    }

    /**
     * Novo fluxo com suporte a imagem via multipart/form-data.
     */
    @Transactional
    public CategoriaResponseDTO createCategoria(
            CategoriaRequestDTO dto,
            MultipartFile imagem
    ) {
        Categoria categoria = new Categoria();

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());
        categoria.setOrdem(dto.ordem());
        categoria.setAtivo(dto.ativo());

        if (imagem != null && !imagem.isEmpty()) {
            String imagemUrl = supabaseStorageService.uploadImagem(imagem, PASTA_CATEGORIAS);
            categoria.setImagemUrl(imagemUrl);
        } else if (dto.imagemUrl() != null && !dto.imagemUrl().isBlank()) {
            // Mantém compatibilidade com o fluxo antigo, caso já envie URL pronta.
            categoria.setImagemUrl(dto.imagemUrl());
        }

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return toResponseDTO(categoriaSalva);
    }

    /**
     * Mantém compatibilidade com o fluxo antigo JSON.
     */
    @Transactional
    public CategoriaResponseDTO updateCategoria(
            Long id,
            CategoriaRequestDTO dto
    ) {
        return updateCategoria(id, dto, null);
    }

    /**
     * Novo fluxo com suporte a imagem via multipart/form-data.
     *
     * Regra:
     * - se vier imagem nova, faz upload e substitui a URL;
     * - se não vier imagem, mantém a imagem atual;
     * - se o DTO vier com imagemUrl preenchida, mantém compatibilidade com fluxo antigo.
     */
    @Transactional
    public CategoriaResponseDTO updateCategoria(
            Long id,
            CategoriaRequestDTO dto,
            MultipartFile imagem
    ) {
        Categoria categoria = buscarCategoriaPorId(id);

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());
        categoria.setOrdem(dto.ordem());
        categoria.setAtivo(dto.ativo());

        if (imagem != null && !imagem.isEmpty()) {
            String imagemUrl = supabaseStorageService.uploadImagem(imagem, PASTA_CATEGORIAS);
            categoria.setImagemUrl(imagemUrl);
        } else if (dto.imagemUrl() != null && !dto.imagemUrl().isBlank()) {
            categoria.setImagemUrl(dto.imagemUrl());
        }

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return toResponseDTO(categoriaAtualizada);
    }

    /**
     * Endpoint separado para atualizar somente a imagem da categoria.
     * Útil para:
     * POST /admin/categorias/{id}/imagem
     */
    @Transactional
    public CategoriaResponseDTO updateImagemCategoria(
            Long id,
            MultipartFile imagem
    ) {
        Categoria categoria = buscarCategoriaPorId(id);

        if (imagem == null || imagem.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nenhuma imagem enviada."
            );
        }

        String imagemUrl = supabaseStorageService.uploadImagem(imagem, PASTA_CATEGORIAS);

        categoria.setImagemUrl(imagemUrl);

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return toResponseDTO(categoriaAtualizada);
    }

    @Transactional
    public CategoriaResponseDTO updateStatus(
            Long id,
            Boolean ativo
    ) {
        Categoria categoria = buscarCategoriaPorId(id);

        if (Boolean.FALSE.equals(ativo)) {
            validarCategoriaSemPedidosEmAndamento(id);
        }

        categoria.setAtivo(ativo);

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return toResponseDTO(categoriaAtualizada);
    }

    @Transactional
    public void deleteCategoria(Long id) {
        Categoria categoria = buscarCategoriaPorId(id);

        validarCategoriaSemPedidosEmAndamento(id);

        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }
}