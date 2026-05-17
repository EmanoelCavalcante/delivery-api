package com.pitsdog.api.produto.controller;


import com.pitsdog.api.produto.dto.AtualizarStatusProdutoDTO;
import com.pitsdog.api.produto.dto.ProdutoRequestDTO;
import com.pitsdog.api.produto.dto.ProdutoResponseDTO;
import com.pitsdog.api.produto.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/produtos")
public class ProdutoAdminController {

    private final ProdutoService produtoService;

    public ProdutoAdminController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> createProduto(
            @RequestBody ProdutoRequestDTO dto
    ){
        ProdutoResponseDTO createdProduto = produtoService.createProduto(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> updateProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequestDTO dto
    ){
        ProdutoResponseDTO updateProduto = produtoService.updateProduto(id, dto);

        return ResponseEntity.ok(updateProduto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProdutoResponseDTO> updateStatusProduto(
            @PathVariable Long id,
            @RequestBody AtualizarStatusProdutoDTO dto
    ){
        return ResponseEntity.ok(produtoService.updateStatus(id, dto.ativo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        produtoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
}

