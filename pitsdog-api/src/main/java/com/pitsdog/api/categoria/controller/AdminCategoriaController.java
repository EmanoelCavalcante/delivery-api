package com.pitsdog.api.categoria.controller;


import com.pitsdog.api.categoria.dto.AtualizarStatusCategoriaDTO;
import com.pitsdog.api.categoria.dto.CategoriaRequestDTO;
import com.pitsdog.api.categoria.dto.CategoriaResponseDTO;
import com.pitsdog.api.categoria.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {

    private final CategoriaService categoriaService;

    public AdminCategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listAll(){
        return ResponseEntity.ok(categoriaService.listAllCategorias());
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(
            @RequestBody CategoriaRequestDTO dto
    ) {

        CategoriaResponseDTO createdCategoria =
                categoriaService.createCategoria(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody CategoriaRequestDTO dto){

        return ResponseEntity.ok(
                categoriaService.updateCategoria(id, dto)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CategoriaResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusCategoriaDTO dto
    ){
        return ResponseEntity.ok(categoriaService.updateStatus(id, dto.ativo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoriaService.deleteCategoria(id);

        return ResponseEntity.noContent().build();
    }
}
