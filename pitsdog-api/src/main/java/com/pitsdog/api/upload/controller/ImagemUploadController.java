package com.pitsdog.api.upload.controller;

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

    public ImagemUploadController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @PostMapping(
            value = "/produtos/{id}/imagem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadImagemProduto(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        String imageUrl = imagemService.atualizarImagemProduto(id, imagem);

        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @PostMapping(
            value = "/combos/{id}/imagem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadImagemCombo(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        String imageUrl = imagemService.atualizarImagemCombo(id, imagem);

        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

}