package com.pitsdog.api.categoria.controller;


import com.pitsdog.api.categoria.dto.CategoriaRequestDTO;
import com.pitsdog.api.categoria.dto.CategoriaResponseDTO;
import com.pitsdog.api.categoria.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listCategoria(){
        List<CategoriaResponseDTO> categorias =
                categoriaService.listCategorias();

        return ResponseEntity.ok(categorias);
    }
}

