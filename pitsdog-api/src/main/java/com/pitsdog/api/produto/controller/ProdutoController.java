package com.pitsdog.api.produto.controller;

import com.pitsdog.api.produto.dto.ProdutoResponseDTO;
import com.pitsdog.api.produto.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }


    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> getProdutosAtivos() {
        return ResponseEntity.ok(produtoService.listProdutosAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> getProdutoById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.getProdutoById(id));
    }
}

