package com.pitsdog.api.upload.service;


import com.pitsdog.api.pedido.entity.Combo;
import com.pitsdog.api.pedido.repository.AdicionalRepository;
import com.pitsdog.api.pedido.repository.ComboRepository;
import com.pitsdog.api.produto.entity.Produto;
import com.pitsdog.api.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagemService {

    private static final String PASTA_PRODUTOS = "produtos";
    private static final String PASTA_COMBOS = "combos";

    private final SupabaseStorageService storageService;
    private final ProdutoRepository produtoRepository;
    private final ComboRepository comboRepository;


    public ImagemService(
            SupabaseStorageService storageService,
            ProdutoRepository produtoRepository,
            ComboRepository comboRepository,
            AdicionalRepository adicionalRepository
    ) {
        this.storageService = storageService;
        this.produtoRepository = produtoRepository;
        this.comboRepository = comboRepository;

    }

    @Transactional
    public String atualizarImagemProduto(Long id, MultipartFile imagem) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado: " + id));

        String imageUrl = storageService.uploadImagem(imagem, PASTA_PRODUTOS);

        produto.setImagemUrl(imageUrl);
        produtoRepository.save(produto);

        return imageUrl;
    }

    @Transactional
    public String atualizarImagemCombo(Long id, MultipartFile imagem) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Combo não encontrado: " + id));

        String imageUrl = storageService.uploadImagem(imagem, PASTA_COMBOS);

        combo.setImagemUrl(imageUrl);
        comboRepository.save(combo);

        return imageUrl;
    }

    public static class EntidadeNaoEncontradaException extends RuntimeException {
        public EntidadeNaoEncontradaException(String message) {
            super(message);
        }
    }
}