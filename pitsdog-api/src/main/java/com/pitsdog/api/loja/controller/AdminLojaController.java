package com.pitsdog.api.loja.controller;

import com.pitsdog.api.loja.dto.AtualizarLojaRequestDTO;
import com.pitsdog.api.loja.dto.LojaStatusReponseDTO;
import com.pitsdog.api.loja.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/loja")
public class AdminLojaController {
    private final LojaService service;

    public AdminLojaController(LojaService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<LojaStatusReponseDTO> buscarStatus(){
        return ResponseEntity.ok(service.buscarStatus());
    }

    @PutMapping("/status")
    public ResponseEntity<LojaStatusReponseDTO> atualizarStatus(
            @Valid @RequestBody AtualizarLojaRequestDTO dto
            ){
        return ResponseEntity.ok(service.atualizarStatus(dto));
    }
}
