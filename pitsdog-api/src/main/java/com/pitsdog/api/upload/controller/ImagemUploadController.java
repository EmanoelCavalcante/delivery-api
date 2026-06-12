package com.pitsdog.api.upload.controller;

import com.pitsdog.api.categoria.dto.CategoriaResponseDTO;
import com.pitsdog.api.categoria.service.CategoriaService;
import com.pitsdog.api.upload.service.ImagemService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ImagemUploadController {

    private final ImagemService imagemService;
    private final CategoriaService categoriaService;

    public ImagemUploadController(
            ImagemService imagemService,
            CategoriaService categoriaService
    ) {
        this.imagemService = imagemService;
        this.categoriaService = categoriaService;
    }

    @PostMapping(
            value = "/categorias/{id}/imagem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CategoriaResponseDTO> uploadImagemCategoria(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        return ResponseEntity.ok(
                categoriaService.updateImagemCategoria(id, imagem)
        );
    }

    @PostMapping(
            value = "/produtos/{id}/imagem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadImagemProduto(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        String imagemUrl = imagemService.atualizarImagemProduto(id, imagem);

        return ResponseEntity.ok(Map.of("imagemUrl", imagemUrl));
    }

    @PostMapping(
            value = "/combos/{id}/imagem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadImagemCombo(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        String imagemUrl = imagemService.atualizarImagemCombo(id, imagem);

        return ResponseEntity.ok(Map.of("imagemUrl", imagemUrl));
    }
}