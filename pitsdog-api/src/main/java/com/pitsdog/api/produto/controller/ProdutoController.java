package com.pitsdog.api.produto.controller;


import com.pitsdog.api.produto.dto.ProdutoRequestDTO;
import com.pitsdog.api.produto.dto.ProdutoResponseDTO;
import com.pitsdog.api.produto.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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


    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> create(
            @RequestBody ProdutoRequestDTO dto
    ){
        ProdutoResponseDTO createdProduto = produtoService.createProduto(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProduto);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> getAllProdutos(){
        return ResponseEntity.ok(produtoService.listAllProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> getProdutoById(@PathVariable Long id){
        return ResponseEntity.ok(produtoService.getProdutoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> updateProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequestDTO dto
    ){
        ProdutoResponseDTO updateProduto = produtoService.updateProduto(id, dto);

        return ResponseEntity.ok(updateProduto);
    }


    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long id){
        return ResponseEntity.noContent().build();
    }
}

