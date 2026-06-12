package com.pitsdog.api.categoria.service;

import com.pitsdog.api.categoria.dto.CategoriaRequestDTO;
import com.pitsdog.api.categoria.dto.CategoriaResponseDTO;
import com.pitsdog.api.categoria.entity.Categoria;
import com.pitsdog.api.categoria.repository.CategoriaRepository;
import com.pitsdog.api.pedido.enums.StatusPedido;
import com.pitsdog.api.upload.service.SupabaseStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

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
     * Novo fluxo com suporte opcional a imagem via multipart/form-data.
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
            log.info(
                    "Criando categoria com imagem. nomeCategoria={}, nomeArquivo={}, tipo={}, tamanho={} bytes",
                    dto.nome(),
                    imagem.getOriginalFilename(),
                    imagem.getContentType(),
                    imagem.getSize()
            );

            String imagemUrl = supabaseStorageService.uploadImagem(imagem, PASTA_CATEGORIAS);
            categoria.setImagemUrl(imagemUrl);

            log.info("Imagem enviada ao criar categoria. imagemUrl={}", imagemUrl);

        } else if (dto.imagemUrl() != null && !dto.imagemUrl().isBlank()) {
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
     * Novo fluxo com suporte opcional a imagem via multipart/form-data.
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
            log.info(
                    "Atualizando categoria id={} com nova imagem. nomeArquivo={}, tipo={}, tamanho={} bytes",
                    id,
                    imagem.getOriginalFilename(),
                    imagem.getContentType(),
                    imagem.getSize()
            );

            String imagemUrl = supabaseStorageService.uploadImagem(imagem, PASTA_CATEGORIAS);
            categoria.setImagemUrl(imagemUrl);

            log.info("Imagem atualizada na categoria id={}. imagemUrl={}", id, imagemUrl);

        } else if (dto.imagemUrl() != null && !dto.imagemUrl().isBlank()) {
            categoria.setImagemUrl(dto.imagemUrl());
        }

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return toResponseDTO(categoriaAtualizada);
    }

    /**
     * Atualiza somente a imagem da categoria.
     *
     * Usado pela rota:
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

        log.info(
                "Upload de imagem recebido para categoria id={}. nomeOriginal={}, contentType={}, tamanho={} bytes",
                id,
                imagem.getOriginalFilename(),
                imagem.getContentType(),
                imagem.getSize()
        );

        try {
            String imagemUrl = supabaseStorageService.uploadImagem(imagem, PASTA_CATEGORIAS);

            if (imagemUrl == null || imagemUrl.isBlank()) {
                log.error("SupabaseStorageService retornou imagemUrl vazia para categoria id={}", id);

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Upload realizado, mas a URL da imagem não foi gerada."
                );
            }

            log.info(
                    "Upload de imagem finalizado para categoria id={}. imagemUrl={}",
                    id,
                    imagemUrl
            );

            categoria.setImagemUrl(imagemUrl);

            Categoria categoriaAtualizada = categoriaRepository.save(categoria);

            log.info(
                    "Categoria id={} atualizada com imagemUrl no banco.",
                    categoriaAtualizada.getId()
            );

            return toResponseDTO(categoriaAtualizada);

        } catch (ResponseStatusException e) {
            log.error(
                    "Erro ao atualizar imagem da categoria id={}. status={}, motivo={}",
                    id,
                    e.getStatusCode(),
                    e.getReason(),
                    e
            );

            throw e;

        } catch (Exception e) {
            log.error(
                    "Erro inesperado ao atualizar imagem da categoria id={}",
                    id,
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao atualizar imagem da categoria."
            );
        }
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